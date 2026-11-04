package me.minebuilders.proxyfork.antiproxy;

import java.io.Serializable;

public enum ProxyResult implements Serializable {
    WHITELISTED,
    UNKNOWN,
    DETECTED,
    ERROR
}
