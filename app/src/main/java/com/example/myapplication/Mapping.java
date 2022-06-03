package com.example.myapplication;

public class Mapping {

    private String nfc_qr;
    private String nfc_uuid;
    private String nrc_qr;
    private String audit_id;
    private String sync;
    private String latitude;
    private String longitude;
    private String date_created;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Mapping(String nfc_qr, String nfc_uuid, String nrc_qr, String audit_id, String sync, String date_created, String latitude, String longitude) {
        super();
        this.nfc_qr = nfc_qr;
        this.nfc_uuid = nfc_uuid;
        this.nrc_qr = nrc_qr;
        this.audit_id = audit_id;
        this.sync = sync;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date_created = date_created;
    }

    public String getNfc_qr() {
        return nfc_qr;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public void setNfc_qr(String nfc_qr) {
        this.nfc_qr = nfc_qr;
    }

    public String getNfc_uuid() {
        return nfc_uuid;
    }

    public void setNfc_uuid(String nfc_uuid) {
        this.nfc_uuid = nfc_uuid;
    }

    public String getNrc_qr() {
        return nrc_qr;
    }

    public void setNrc_qr(String nrc_qr) {
        this.nrc_qr = nrc_qr;
    }

    public String getAudit_id() {
        return audit_id;
    }

    public void setAudit_id(String audit_id) {
        this.audit_id = audit_id;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }
}
