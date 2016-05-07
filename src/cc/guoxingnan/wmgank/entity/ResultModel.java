package cc.guoxingnan.wmgank.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResultModel implements Serializable {
    @SerializedName("error")
    private boolean error;

    @SerializedName("results")
    private List<GankModel> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<GankModel> getResults() {
        return results;
    }

    public void setResults(List<GankModel> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ResultModel{" +
                "error=" + error +
                ", results=" + results +
                '}';
    }
}
