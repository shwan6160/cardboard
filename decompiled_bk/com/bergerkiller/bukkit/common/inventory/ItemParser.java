/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package com.bergerkiller.bukkit.common.inventory;

import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import com.bergerkiller.bukkit.common.inventory.CommonItemStack;
import com.bergerkiller.bukkit.common.inventory.ItemParserMetaRule;
import com.bergerkiller.bukkit.common.utils.ItemUtil;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.bukkit.common.utils.MaterialUtil;
import com.bergerkiller.bukkit.common.utils.ParseUtil;
import com.bergerkiller.bukkit.common.utils.StringUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemParser
implements Predicate<CommonItemStack> {
    public static final char METADATA_CHAR = '$';
    public static final char STACK_MULTIPLIER = '^';
    public static final char[] MULTIPLIER_SIGNS = new char[]{'x', 'X', '*', ' ', '@', '^'};
    private int data;
    private Material type;
    private int amount;
    private List<ItemParserMetaRule> rules;

    public ItemParser(Material type) {
        this(type, -1);
    }

    public ItemParser(Material type, int amount) {
        this(type, amount, -1);
    }

    public ItemParser(Material type, int amount, int data) {
        this.amount = amount;
        this.data = data;
        this.type = type;
        this.rules = Collections.emptyList();
    }

    public ItemParser(Material type, int amount, int data, List<ItemParserMetaRule> metaRules) {
        this.amount = amount;
        this.data = data;
        this.type = type;
        this.rules = metaRules;
    }

    private ItemParser() {
        this.data = -1;
        this.type = null;
        this.amount = 1;
        this.rules = Collections.emptyList();
    }

    public static ItemParser parse(String fullname) {
        fullname = fullname.trim();
        String amountStr = null;
        boolean isStackMultiplier = false;
        int index = StringUtil.firstIndexOf(fullname, MULTIPLIER_SIGNS);
        if (index != -1) {
            amountStr = fullname.substring(0, index);
            try {
                Integer.parseInt(amountStr);
                isStackMultiplier = fullname.charAt(index) == '^';
                fullname = fullname.substring(index + 1);
            }
            catch (NumberFormatException ex) {
                amountStr = null;
            }
        }
        ItemParser parser = ItemParser.parse(fullname, amountStr);
        if (isStackMultiplier) {
            parser = parser.multiplyAmount(parser.getMaxStackSize());
        }
        return parser;
    }

    public static ItemParser parse(String name, String amount) {
        int index = StringUtil.firstIndexOf(name, ':', '$');
        if (index == -1) {
            return ItemParser.parse(name, null, amount);
        }
        int data_index = index;
        if (name.charAt(index) == ':') {
            ++data_index;
        }
        return ItemParser.parse(name.substring(0, index), name.substring(data_index), amount);
    }

    @Deprecated
    public static ItemParser parse(String name, String dataname, String amount) {
        int index;
        ItemParser parser = new ItemParser();
        parser.amount = ParseUtil.parseInt(amount, -1);
        String dataname_data = dataname;
        String dataname_meta = null;
        if (dataname_data != null) {
            index = dataname_data.indexOf(36);
            if (index == 0) {
                dataname_meta = dataname_data.substring(1);
                dataname_data = null;
            } else if (index > 0) {
                dataname_meta = dataname_data.substring(index + 1);
                dataname_data = dataname_data.substring(0, index);
            }
        }
        if (LogicUtil.nullOrEmpty(name)) {
            parser.type = null;
        } else if (dataname_data == null) {
            parser.type = ParseUtil.parseMaterial(name, Material.AIR, false);
            if (parser.type == null) {
                parser.type = ParseUtil.parseMaterial(name, Material.AIR, true);
            }
        } else {
            parser.type = ParseUtil.parseMaterial(name, Material.AIR, true);
        }
        if (parser.hasType() && !LogicUtil.nullOrEmpty(dataname_data)) {
            parser.data = ParseUtil.parseMaterialData(dataname_data, parser.type, -1);
        }
        if (!LogicUtil.nullOrEmpty(dataname_meta)) {
            parser.rules = new ArrayList<ItemParserMetaRule>(4);
            index = 0;
            do {
                String ruleStr;
                int end_index;
                if ((end_index = dataname_meta.indexOf(36, index)) == -1) {
                    ruleStr = dataname_meta.substring(index);
                    index = -1;
                } else {
                    ruleStr = dataname_meta.substring(index, end_index);
                    index = end_index + 1;
                }
                parser.rules.add(ItemParserMetaRule.parse(ruleStr));
            } while (index != -1);
        }
        return parser;
    }

    @Override
    public boolean test(CommonItemStack commonItemStack) {
        return this.match(commonItemStack.toBukkit());
    }

    public boolean match(ItemStack stack) {
        return this.match(CommonItemStack.of(stack));
    }

    public boolean match(CommonItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES ? !this.match(stack.getType(), 0) : !this.match(stack.getType(), stack.toBukkit().getDurability())) {
            return false;
        }
        for (ItemParserMetaRule rule : this.rules) {
            if (rule.match(stack)) continue;
            return false;
        }
        return true;
    }

    public boolean match(BlockData blockData) {
        if (this.hasType() && !blockData.isType(this.getType())) {
            return false;
        }
        return !this.hasData() || blockData.getRawData() == this.getData();
    }

    @Deprecated
    public boolean match(Material type, int data) {
        Material self = this.type;
        if (self != null) {
            boolean selfIsLegacy;
            boolean typeIsLegacy = MaterialUtil.isLegacyType(type);
            if (typeIsLegacy != (selfIsLegacy = MaterialUtil.isLegacyType(self))) {
                if (!typeIsLegacy) {
                    if (MaterialUtil.isBlock(type)) {
                        BlockData block = BlockData.fromMaterial(type);
                        type = block.getLegacyType();
                        data = block.getRawData();
                    } else {
                        type = CommonLegacyMaterials.toLegacy(type);
                        data = 0;
                    }
                } else {
                    self = CommonLegacyMaterials.toLegacy(self);
                }
            }
            if (type != self) {
                return false;
            }
        }
        if (this.hasData() && MaterialUtil.isLegacyType(type)) {
            if (this.hasType() && CommonLegacyMaterials.getMaterialName(this.getType()).equals("LEGACY_LEAVES")) {
                data &= 3;
            }
            if (data != this.getData()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasAmount() {
        return this.amount >= 0;
    }

    @Deprecated
    public boolean hasData() {
        return this.data >= 0;
    }

    public boolean hasType() {
        return this.type != null;
    }

    public int getData() {
        return this.data;
    }

    public int getAmount() {
        return this.amount;
    }

    public Material getType() {
        return this.type;
    }

    public List<ItemParserMetaRule> getMetaRules() {
        return Collections.unmodifiableList(this.rules);
    }

    public ItemStack getItemStack() {
        return this.getItemStack(this.amount);
    }

    public ItemStack getItemStack(int amount) {
        if (!this.hasType()) {
            return null;
        }
        if (amount <= 0) {
            amount = this.getMaxStackSize();
        }
        if (MaterialUtil.isBlock(this.type)) {
            BlockData block = this.hasData() ? BlockData.fromMaterialData(this.type, this.data) : BlockData.fromMaterial(this.type);
            return block.createItem(amount);
        }
        if (this.hasData()) {
            return new ItemStack(this.type, amount, (short)this.data);
        }
        return new ItemStack(this.type, amount);
    }

    public int getMaxStackSize() {
        return ItemUtil.getMaxSize(this.getType(), 64);
    }

    public ItemParser setData(int data) {
        ItemParser clone = this.cloneParser();
        this.data = data;
        return clone;
    }

    public ItemParser setAmount(int amount) {
        ItemParser clone = this.cloneParser();
        clone.amount = amount;
        return clone;
    }

    public ItemParser setMetaRules(List<ItemParserMetaRule> rules) {
        ItemParser clone = this.cloneParser();
        clone.rules = rules.isEmpty() ? Collections.emptyList() : new ArrayList<ItemParserMetaRule>(rules);
        return clone;
    }

    public ItemParser multiplyAmount(int amount) {
        if (this.hasAmount()) {
            amount *= this.getAmount();
        }
        return this.setAmount(amount);
    }

    private ItemParser cloneParser() {
        return new ItemParser(this.type, this.amount, this.data, (List<ItemParserMetaRule>)(this.rules.isEmpty() ? Collections.emptyList() : new ArrayList<ItemParserMetaRule>(this.rules)));
    }

    public String toString() {
        StringBuilder rval = new StringBuilder();
        if (this.hasAmount()) {
            rval.append(this.amount).append(" of ");
        }
        if (this.hasType()) {
            rval.append(this.type.toString().toLowerCase());
            if (this.hasData()) {
                rval.append(':').append(this.data);
            }
        } else {
            rval.append("any type");
        }
        if (!this.rules.isEmpty()) {
            rval.append('$');
            for (ItemParserMetaRule element : this.rules) {
                rval.append('$');
                rval.append(element.toString());
            }
        }
        return rval.toString();
    }
}

