package com.poptopia.promotion.constants;

public enum CodeConstants {
WM("wm"),
TOS("tos"),
POOL("pool");

private String status;

private CodeConstants(String status) {
	this.status = status;
}

public String getStatus() {
	return status;
}

}
