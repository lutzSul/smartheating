
package de.lutz.smartheating.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @SerializedName("85")
    @Expose
    private de.lutz.smartheating.model._85 _85;

    public de.lutz.smartheating.model._85 get85() {
        return _85;
    }

    public void set85(de.lutz.smartheating.model._85 _85) {
        this._85 = _85;
    }

}
