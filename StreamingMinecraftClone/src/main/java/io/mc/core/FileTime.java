package io.mc.core;

public class FileTime {
    long creation;
    long lastWrite;
    long lastAccess;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (creation ^ (creation >>> 32));
        result = prime * result + (int) (lastWrite ^ (lastWrite >>> 32));
        result = prime * result + (int) (lastAccess ^ (lastAccess >>> 32));
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof FileTime))
            return false;
        FileTime other = (FileTime) obj;
        if (creation != other.creation)
            return false;
        if (lastWrite != other.lastWrite)
            return false;
        if (lastAccess != other.lastAccess)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return "FileTime [creation=" + creation + ", lastWrite=" + lastWrite + ", lastAccess=" + lastAccess + "]";
    }

    
    
}
