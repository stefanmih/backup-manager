package com.backupmanager.app.utils;

public class File {
    private String name;
    private String localPath;
    private String remotePath;
    private long size;
    private String hash;
    private boolean directory;
    private String extension;

    public File() {
    }

    public File(String name, String localPath, String remotePath, long size, String hash, boolean directory, String extension) {
        this.name = name;
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.size = size;
        this.hash = hash;
        this.directory = directory;
        this.extension = extension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", localPath='" + localPath + '\'' +
                ", remotePath='" + remotePath + '\'' +
                ", size=" + size +
                ", hash='" + hash + '\'' +
                ", directory=" + directory +
                ", extension='" + extension + '\'' +
                '}';
    }
}
