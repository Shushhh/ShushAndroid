package com.example.shushandroid;

/**
 * @apiNote Shush Object helper class
 * @author  Akash Veerappan
 * @version 1.0
 * @since   2020-7-18
 * @resources
 */
public class ShushObject {
    private String name;
    private String type;
    private String data;
    private String supplementalData;

    /**
     *
     * @param name
     * @param type
     * @param data
     * @param supplementalData
     */
    public ShushObject(String name, String type, String data, String supplementalData) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.supplementalData = supplementalData;
    }

    /**
     *
     */
    public ShushObject() {}

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getData() {
        return data;
    }

    /**
     *
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     *
     * @return
     */
    public String getSupplementalData() {
        return supplementalData;
    }

    /**
     *
     * @param supplementalData
     */
    public void setSupplementalData(String supplementalData) {
        this.supplementalData = supplementalData;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return "ShushObject{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", supplementalData='" + supplementalData + '\'' +
                '}';
    }

    /**
     *
     */
    enum ShushObjectType {
        LOCATION("Location"), TIME("Time");

        private String description;

        /**
         *
         * @param description
         */
        ShushObjectType(String description) {
            this.description = description;
        }

        /**
         *
         * @return
         */
        public String getDescription() {
            return description;
        }

        /**
         *
         * @param description
         */
        public void setDescription(String description) {
            this.description = description;
        }
    }
}
