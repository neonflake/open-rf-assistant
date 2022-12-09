package hu.open.assistant.rf.model;

import hu.open.assistant.commons.util.TextHelper;
import hu.open.assistant.commons.util.ValueHelper;

import java.util.List;

/**
 * A logical representation of a user equipment (UE). It stores type, manufacturer, position, alignment, image, network,
 * generic and tester related information. Type, manufacturer and position values are direct copies from the actual RF
 * profiles, both for the CMU and the CMW variant (if they exist). It stores its default values for modification check.
 */
public class Equipment implements Comparable<Equipment> {

    private String type = "";
    private String manufacturer = "";
    private String[] supportedNetworks = new String[0];
    private String cmuBox = "";
    private String cmuPosition = "";
    private String cmuPositionDetail = "";
    private String cmuPositionImage = "";
    private String cmwBox = "";
    private String cmwPosition = "";
    private String cmwPositionDetail = "";
    private String cmwPositionImage = "";
    private String genericInfo = "";
    private String usage = "";
    private String cmuInfo = "";
    private String cmwInfo = "";
    private List<Object> defaultValues;

    public Equipment() {

    }

    public void initDefaultValues() {
        defaultValues = createValues();
    }

    public List<Object> createValues() {
        return new ValueHelper()
                .add(supportedNetworks)
                .add(cmuBox)
                .add(cmuPosition)
                .add(cmuPositionDetail)
                .add(cmuPositionImage)
                .add(cmwBox)
                .add(cmwPosition)
                .add(cmwPositionDetail)
                .add(cmwPositionImage)
                .add(genericInfo)
                .add(usage)
                .add(cmuInfo)
                .add(cmwInfo)
                .getValues();
    }

    public String getCmuBox() {
        return cmuBox;
    }

    public void setCmuBox(String cmuBox) {
        this.cmuBox = cmuBox;
    }

    public String getCmuPosition() {
        return cmuPosition;
    }

    public void setCmuPosition(String cmuPosition) {
        this.cmuPosition = cmuPosition;
    }

    public String getCmwBox() {
        return cmwBox;
    }

    public void setCmwBox(String cmwBox) {
        this.cmwBox = cmwBox;
    }

    public String getCmwPosition() {
        return cmwPosition;
    }

    public void setCmwPosition(String cmwPosition) {
        this.cmwPosition = cmwPosition;
    }

    public boolean isModified() {
        return !defaultValues.equals(createValues());
    }

    public void setSupportedNetworks(String[] networks) {
        this.supportedNetworks = networks;
    }

    public String[] getSupportedNetworks() {
        return supportedNetworks;
    }

    public String getCmuPositionDetail() {
        return cmuPositionDetail;
    }

    public void setCmuPositionDetail(String cmuPositionDetail) {
        this.cmuPositionDetail = cmuPositionDetail;
    }

    public String getCmwPositionDetail() {
        return cmwPositionDetail;
    }

    public void setCmwPositionDetail(String cmwPositionDetail) {
        this.cmwPositionDetail = cmwPositionDetail;
    }

    public String getGenericInfo() {
        return genericInfo;
    }

    public void setGenericInfo(String genericInfo) {
        this.genericInfo = genericInfo;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getCmuInfo() {
        return cmuInfo;
    }

    public void setCmuInfo(String cmuInfo) {
        this.cmuInfo = cmuInfo;
    }

    public String getCmwInfo() {
        return cmwInfo;
    }

    public void setCmwInfo(String cmwInfo) {
        this.cmwInfo = cmwInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getCmuPositionAutoImage() {
        if (cmuBox != null) {
            return cmuBox + "_" + cmuPosition + "_" + cmuPositionDetail;
        }
        return "";
    }

    public String getCmuPositionImage() {
        return cmuPositionImage;
    }

    public void setCmuPositionImage(String cmuPositionImage) {
        this.cmuPositionImage = cmuPositionImage;
    }

    public String getCmwPositionAutoImage() {
        if (cmwBox != null) {
            if (cmwPositionImage.contains("auto-cmw")) {
                return "CMW-Z11_" + cmwPosition + "_" + cmwPositionDetail;
            } else if (cmwPositionImage.contains("auto-aero")) {
                return "Aeroflex_4933_" + cmwPosition + "_" + cmwPositionDetail;
            }
        }
        return "";
    }

    public String getCmwPositionImage() {
        return cmwPositionImage;
    }

    public void setCmwPositionImage(String cmwPositionImage) {
        this.cmwPositionImage = cmwPositionImage;
    }

    public String getName() {
        return manufacturer + " " + type;
    }

    public String getInfo() {
        String text;
        text = "Általános információ:\n   " + genericInfo + "\n\nCMU-s műszerre vonatkozó:\n   " + cmuInfo + "\n\nCMW-s műszerre vonatkozó:\n   "
                + cmwInfo + "\n\nTámogatott hálózatok: " + TextHelper.stringArrayToCommaSeparatedString(supportedNetworks).replace(",", ", ");
        return text;
    }

    @Override
    public int compareTo(Equipment other) {
        return getName().compareTo(other.getName());
    }
}
