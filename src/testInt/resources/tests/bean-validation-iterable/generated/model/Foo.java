/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-core.
 * TEST ONLY.
 */

package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class Foo {

    @Valid
    @JsonProperty("bars")
    private Bar[] bars;

    public Bar[] getBars() {
        return bars;
    }

    public void setBars(Bar[] bars) {
        this.bars = bars;
    }

}
