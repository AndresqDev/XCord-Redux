package me.minebuilders.proxyfork.antiproxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import me.minebuilders.proxyfork.antiproxy.ProxyResult;
import xcord.StringInterner;

public class ProxyCheckResult
implements Externalizable {
    private ProxyResult result;
    private String countryCode = null;
    private String asn = null;
    private long timestamp = System.currentTimeMillis();

    public ProxyCheckResult() {
        this.result = ProxyResult.UNKNOWN;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public String getAsn() {
        return this.asn;
    }

    public ProxyResult getResult() {
        return this.result;
    }

    public void setResult(ProxyResult proxyResult) {
        this.result = proxyResult;
    }

    public void setCountryCode(String string) {
        this.countryCode = StringInterner.intern(string);
    }

    public void setAsn(String string) {
        this.asn = StringInterner.intern(string);
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeByte(this.result.ordinal());
        objectOutput.writeUTF(this.countryCode);
        objectOutput.writeUTF(this.asn);
        objectOutput.writeLong(this.timestamp);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.result = ProxyResult.values()[objectInput.readByte()];
        this.countryCode = StringInterner.intern(objectInput.readUTF());
        this.asn = StringInterner.intern(objectInput.readUTF());
        this.timestamp = objectInput.readLong();
    }
}

