/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package com.bergerkiller.bukkit.common;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.dep.gson.Gson;
import com.bergerkiller.bukkit.common.dep.gson.JsonIOException;
import com.bergerkiller.bukkit.common.dep.gson.JsonSyntaxException;
import com.bergerkiller.bukkit.common.dep.gson.annotations.SerializedName;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.io.ByteArrayIOStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Hastebin {
    private final Executor _executor;
    private final JavaPlugin _plugin;
    private Session _uploadSession;

    public Hastebin(JavaPlugin plugin) {
        this(plugin, null);
    }

    public Hastebin(JavaPlugin plugin, String serverURL) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin can not be null");
        }
        this._executor = Executors.newFixedThreadPool(2);
        this._plugin = plugin;
        this._uploadSession = new Session(plugin, serverURL);
    }

    public void setServer(String serverURL) {
        this._uploadSession = new Session(this._plugin, serverURL);
    }

    public CompletableFuture<UploadResult> upload(String content) {
        CompletableFuture<UploadResult> result = new CompletableFuture<UploadResult>();
        Session session = this._uploadSession;
        this._executor.execute(() -> {
            try {
                HttpURLConnection con;
                byte[] contents_bytes = null;
                ByteArrayOutputStream contents_bytes_compressed = null;
                do {
                    con = session.createRequest("POST", "/documents");
                    con.setDoOutput(true);
                    con.setRequestProperty("Accept", "application/json, */*; q=0.01");
                    if (session.getCapabilities().requestContentEncoding) {
                        if (contents_bytes_compressed == null) {
                            contents_bytes_compressed = new ByteArrayOutputStream();
                            try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new GZIPOutputStream(contents_bytes_compressed), "UTF-8");){
                                writer.write(content);
                            }
                        }
                        con.setRequestProperty("Content-Encoding", "gzip");
                        con.setFixedLengthStreamingMode(contents_bytes_compressed.size());
                        try (OutputStream output1 = con.getOutputStream();){
                            contents_bytes_compressed.writeTo(output1);
                        }
                    }
                    if (contents_bytes == null) {
                        contents_bytes = content.getBytes(Charset.forName("UTF-8"));
                    }
                    con.setFixedLengthStreamingMode(contents_bytes.length);
                    try (OutputStream output2 = con.getOutputStream();){
                        output2.write(contents_bytes);
                    }
                } while (session.handleRedirect(con));
                HastebinUploadResponse response = Hastebin.decodeGSON(con, HastebinUploadResponse.class);
                if (response.key == null) {
                    throw new InvalidServerResponseException("No key");
                }
                this.complete(result, new UploadResult(true, session.createURL("/" + response.key).toString(), null));
            }
            catch (IOException ex1) {
                this.complete(result, new UploadResult(false, null, "I/O Exception occurred: " + ex1.getMessage()));
            }
            catch (InvalidServerURLException ex2) {
                this.complete(result, new UploadResult(false, null, "Invalid Server URL: " + session.getServer()));
            }
            catch (InvalidServerResponseException ex3) {
                this.complete(result, new UploadResult(false, null, ex3.getMessage()));
            }
            catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.WARNING, "Unhandled error uploading to hastebin", t);
                this.complete(result, new UploadResult(false, null, "Error occurred: " + t.getMessage()));
            }
        });
        return result;
    }

    public CompletableFuture<DownloadResult> download(String url) {
        CompletableFuture<DownloadResult> result = new CompletableFuture<DownloadResult>();
        Session session = new Session(this._plugin, url);
        this._executor.execute(() -> {
            try {
                HttpURLConnection con;
                String raw_path = session.findRawPath();
                do {
                    con = session.createRequest("GET", raw_path);
                    con.setRequestProperty("Accept-Encoding", "gzip");
                } while (session.handleRedirect(con));
                int expectedContentSize = con.getContentLength();
                if (expectedContentSize <= 0) {
                    expectedContentSize = 1024;
                }
                ByteArrayIOStream contentBuffer = new ByteArrayIOStream(expectedContentSize);
                if ("gzip".equals(con.getContentEncoding())) {
                    try (GZIPInputStream input1 = new GZIPInputStream(con.getInputStream());){
                        contentBuffer.readFrom(input1);
                    }
                }
                try (InputStream input2 = con.getInputStream();){
                    contentBuffer.readFrom(input2);
                }
                this.complete(result, DownloadResult.content(url, contentBuffer));
            }
            catch (IOException ex1) {
                this.complete(result, DownloadResult.error(url, "I/O Exception occurred: " + ex1.getMessage()));
            }
            catch (InvalidServerURLException ex2) {
                this.complete(result, DownloadResult.error(url, "Invalid URL: " + session.getServer()));
            }
            catch (InvalidServerResponseException ex3) {
                this.complete(result, DownloadResult.error(url, ex3.getMessage()));
            }
            catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.WARNING, "Unhandled error downloading from hastebin", t);
                this.complete(result, DownloadResult.error(url, "Error occurred: " + t.getMessage()));
            }
        });
        return result;
    }

    private final <T> void complete(CompletableFuture<T> future, T result) {
        if (!this._plugin.isEnabled()) {
            Logging.LOGGER_NETWORK.warning("Hastebin operation for " + this._plugin.getName() + " completed after plugin was disabled");
            return;
        }
        int id = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this._plugin, () -> future.complete(result));
        if (id == -1) {
            Logging.LOGGER_NETWORK.warning("Hastebin operation for " + this._plugin.getName() + " completed but running the callbacks failed");
        }
    }

    private static <T> T decodeGSON(HttpURLConnection connection, Class<T> type) throws InvalidServerResponseException, IOException {
        if (connection.getResponseCode() != 200) {
            throw new InvalidServerResponseException("Non-OK Status Code " + connection.getResponseCode());
        }
        try {
            T decoded;
            if ("gzip".equals(connection.getHeaderField("Content-Encoding"))) {
                try (InputStreamReader reader = new InputStreamReader(new GZIPInputStream(connection.getInputStream()));){
                    decoded = new Gson().fromJson((Reader)reader, type);
                }
            }
            try (InputStreamReader reader = new InputStreamReader(connection.getInputStream());){
                decoded = new Gson().fromJson((Reader)reader, type);
            }
            if (decoded == null) {
                throw new InvalidServerResponseException("Empty response received (EOF)");
            }
            return decoded;
        }
        catch (JsonIOException ex) {
            throw new IOException("Failed to read JSON: " + ex.getMessage());
        }
        catch (JsonSyntaxException ex) {
            throw new InvalidServerResponseException("Response is not valid JSON");
        }
    }

    private static class Session {
        private final JavaPlugin _plugin;
        private final String _server;
        private URL _serverURL;
        private String _userAgentString;
        private HastebinCapabilitiesResponse _capabilities;
        private int _numRedirects;

        public Session(JavaPlugin plugin, String serverURL) {
            this._plugin = plugin;
            this._server = serverURL;
            this._serverURL = null;
            this._userAgentString = null;
            this._capabilities = null;
            if (this._server != null) {
                try {
                    this._serverURL = new URL(this._server);
                }
                catch (MalformedURLException e1) {
                    try {
                        this._serverURL = new URL("http://" + this._server);
                    }
                    catch (MalformedURLException malformedURLException) {
                        // empty catch block
                    }
                }
            }
        }

        public String getServer() {
            return this._server;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public HastebinCapabilitiesResponse getCapabilities() throws InvalidServerURLException, InvalidServerResponseException, IOException {
            HttpURLConnection connection;
            Session session = this;
            synchronized (session) {
                if (this._capabilities != null) {
                    return this._capabilities;
                }
            }
            do {
                connection = this.createRequest("GET", "/capabilities");
                connection.setRequestProperty("Accept", "application/json, */*; q=0.01");
            } while (this.handleRedirect(connection));
            HastebinCapabilitiesResponse response = connection.getResponseCode() != 200 || !"application/json".equals(connection.getContentType()) ? new HastebinCapabilitiesResponse() : (HastebinCapabilitiesResponse)Hastebin.decodeGSON(connection, HastebinCapabilitiesResponse.class);
            Session session2 = this;
            synchronized (session2) {
                if (this._capabilities == null) {
                    this._capabilities = response;
                }
                return this._capabilities;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public HttpURLConnection createRequest(String method, String path) throws InvalidServerURLException, IOException {
            URLConnection url_connection = this.createURL(path).openConnection();
            if (!(url_connection instanceof HttpURLConnection)) {
                throw new InvalidServerURLException();
            }
            HttpURLConnection connection = (HttpURLConnection)url_connection;
            connection.setRequestMethod(method);
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);
            connection.setRequestProperty("X-Requested-With", "HttpURLConnection");
            connection.setRequestProperty("Accept-Encoding", "gzip");
            Session session = this;
            synchronized (session) {
                if (this._userAgentString == null) {
                    this._userAgentString = "BKCommonLib/" + CommonPlugin.getInstance().getVersion() + " " + this._plugin.getName() + "/" + this._plugin.getDescription().getVersion() + " Java/" + System.getProperty("java.version");
                }
                connection.setRequestProperty("User-Agent", this._userAgentString);
            }
            return connection;
        }

        public synchronized boolean handleRedirect(HttpURLConnection connection) throws IOException, InvalidServerResponseException {
            int status = connection.getResponseCode();
            if (status != 301 && status != 302) {
                this._numRedirects = 0;
                return false;
            }
            if (++this._numRedirects > 10) {
                throw new InvalidServerResponseException("Maximum number of HTTP redirects reached");
            }
            String redirect_url_str = connection.getHeaderField("Location");
            if (redirect_url_str == null) {
                throw new InvalidServerResponseException("HTTP Redirect without Location header");
            }
            try {
                URL url = new URL(redirect_url_str);
                this._serverURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
                return true;
            }
            catch (MalformedURLException e) {
                throw new InvalidServerResponseException("HTTP Redirect Location is malformed (" + redirect_url_str + ")");
            }
        }

        public synchronized String findRawPath() throws InvalidServerURLException {
            int ext_idx;
            if (this._serverURL == null) {
                throw new InvalidServerURLException();
            }
            String path = this._serverURL.getPath();
            if (path == null || path.isEmpty() || path.charAt(0) != '/') {
                throw new InvalidServerURLException();
            }
            int last_slash_idx = path.lastIndexOf(47);
            if (last_slash_idx == -1) {
                throw new InvalidServerURLException();
            }
            while ((ext_idx = path.lastIndexOf(46)) != -1 && ext_idx > last_slash_idx) {
                path = path.substring(0, ext_idx);
            }
            if (path.startsWith("/documents/")) {
                path = path.substring(10);
            } else if (path.startsWith("/download/")) {
                path = path.substring(9);
            } else if (path.startsWith("/raw/")) {
                path = path.substring(4);
            }
            if (path.indexOf(47, 1) != -1) {
                throw new InvalidServerURLException();
            }
            return "/raw" + path;
        }

        public synchronized URL createURL(String path) throws InvalidServerURLException {
            if (this._serverURL == null) {
                throw new InvalidServerURLException();
            }
            try {
                return new URL(this._serverURL.getProtocol(), this._serverURL.getHost(), this._serverURL.getPort(), path);
            }
            catch (MalformedURLException e) {
                throw new InvalidServerURLException();
            }
        }
    }

    private static class InvalidServerResponseException
    extends Exception {
        private static final long serialVersionUID = 1542358791115174559L;

        public InvalidServerResponseException(String what) {
            super("Invalid response received from server: " + what);
        }
    }

    public static class DownloadResult {
        private final boolean _success;
        private final String _url;
        private final ByteArrayIOStream _content;
        private final String _error;

        private DownloadResult(boolean success, String url, ByteArrayIOStream content, String error) {
            this._success = success;
            this._url = url;
            this._content = content;
            this._error = error;
        }

        public static DownloadResult error(String url, String error) {
            return new DownloadResult(false, url, null, error);
        }

        public static DownloadResult content(String url, ByteArrayIOStream content) {
            return new DownloadResult(true, url, content, null);
        }

        public boolean success() {
            return this._success;
        }

        public String url() {
            return this._url;
        }

        public String content() {
            try {
                return this._content.toString("UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                return "";
            }
        }

        public InputStream contentInputStream() {
            return this._content.toInputStream();
        }

        public BasicConfiguration contentYAML() throws IOException {
            BasicConfiguration config = new BasicConfiguration();
            config.loadFromStream(this.contentInputStream());
            return config;
        }

        public String error() {
            return this._error;
        }
    }

    private static class InvalidServerURLException
    extends Exception {
        private static final long serialVersionUID = -3102529517837518121L;

        private InvalidServerURLException() {
        }
    }

    private static class HastebinCapabilitiesResponse {
        @SerializedName(value="request-content-encoding")
        public boolean requestContentEncoding = false;

        private HastebinCapabilitiesResponse() {
        }
    }

    private static class HastebinUploadResponse {
        public String key;

        private HastebinUploadResponse() {
        }
    }

    public static class UploadResult {
        private final boolean _success;
        private final String _url;
        private final String _error;

        private UploadResult(boolean success, String url, String error) {
            this._success = success;
            this._url = url;
            this._error = error;
        }

        public boolean success() {
            return this._success;
        }

        public String url() {
            return this._url;
        }

        public String error() {
            return this._error;
        }
    }
}

