/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-core.
 * TEST ONLY.
 */

package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;

public class Bar {

    @Size(max = 10)
    @JsonProperty("bar")
    private String bar;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

}
