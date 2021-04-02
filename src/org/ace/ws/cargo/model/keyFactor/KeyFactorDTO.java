package org.ace.ws.cargo.model.keyFactor;

import java.io.Serializable;

/**
 * @author Hlaing Win Tunn
 *
 */
public class KeyFactorDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String value;
	private String keyfactor;
	private int version;

	public KeyFactorDTO() {

	}

	public KeyFactorDTO(String id, String value, String keyfactor, int version) {
		this.id = id;
		this.value = value;
		this.keyfactor = keyfactor;
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKeyfactor() {
		return keyfactor;
	}

	public void setKeyfactor(String keyfactor) {
		this.keyfactor = keyfactor;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
