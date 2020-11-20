package com.elikill58.galloraapi.api.yaml;

public class LoaderOptions {
	private boolean allowDuplicateKeys;

	public LoaderOptions() {
		this.allowDuplicateKeys = true;
	}

	public boolean isAllowDuplicateKeys() {
		return this.allowDuplicateKeys;
	}

	public void setAllowDuplicateKeys(final boolean allowDuplicateKeys) {
		this.allowDuplicateKeys = allowDuplicateKeys;
	}
}
