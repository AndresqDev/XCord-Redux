package me.minebuilders.proxyfork.antiproxy;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import xcord.util.StringInterner;

public class ProxyCheckResult implements Externalizable {
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

    public void setResult(ProxyResult result) {
        this.result = result;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = StringInterner.intern(countryCode);
    }

    public void setAsn(String asn) {
        this.asn = StringInterner.intern(asn);
    }

    @Override
    public void writeExternal(ObjectOutput output) throws IOException {
        output.writeByte(this.result.ordinal());
        output.writeUTF(this.countryCode);
        output.writeUTF(this.asn);
        output.writeLong(this.timestamp);
    }

    @Override
    public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
        this.result = ProxyResult.values()[input.readByte()];
        this.countryCode = StringInterner.intern(input.readUTF());
        this.asn = StringInterner.intern(input.readUTF());
        this.timestamp = input.readLong();
    }
}
