package com.example.shushandroid;

public class ShushObject {
    private String name;
    private String type;
    private String data;
    private String supplementalData;

    public ShushObject(String name, String type, String data, String supplementalData) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.supplementalData = supplementalData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSupplementalData() {
        return supplementalData;
    }

    public void setSupplementalData(String supplementalData) {
        this.supplementalData = supplementalData;
    }

    @Override
    public String toString() {
        return "ShushObject{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", data='" + data + '\'' +
                ", supplementalData='" + supplementalData + '\'' +
                '}';
    }

    enum ShushObjectType {
        LOCATION("Location"), TIME("Time");

        private String description;

        ShushObjectType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
