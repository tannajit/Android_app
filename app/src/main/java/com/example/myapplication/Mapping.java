package com.example.myapplication;

public class Mapping {

    private String nfc_qr;
    private String nfc_uuid;
    private String nrc_qr;
    private String audit_id;
    private String sync;

    public Mapping(String nfc_qr, String nfc_uuid, String nrc_qr, String audit_id, String sync) {
        super();
        this.nfc_qr = nfc_qr;
        this.nfc_uuid = nfc_uuid;
        this.nrc_qr = nrc_qr;
        this.audit_id = audit_id;
        this.sync = sync;
    }
}
