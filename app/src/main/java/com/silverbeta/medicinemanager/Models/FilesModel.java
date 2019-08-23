package com.silverbeta.medicinemanager.Models;

import java.io.Serializable;

public class FilesModel implements Serializable {

    String fileUri,id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
}
