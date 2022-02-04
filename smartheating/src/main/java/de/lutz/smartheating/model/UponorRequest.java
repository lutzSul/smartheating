
package de.lutz.smartheating.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UponorRequest {

    @SerializedName("jsonrpc")
    @Expose
    private String jsonrpc;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("params")
    @Expose
    private Params params;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Params getParams() {
        return params;
    }

    public void setParams(Params params) {
        this.params = params;
    }

}
