package harp.unknownfielddemo.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Document
public class TestObject {

  @Id
  private String id;
  
  private String knownField;

  @JsonIgnore
  private Map<String, Object> unknownFields = new HashMap<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getKnownField() {
    return knownField;
  }

  public void setKnownField(String aKnownField) {
    this.knownField = aKnownField;
  }

  @JsonAnyGetter
  public Map<String, Object> getUnknownFields() {
    return unknownFields;
  }

  @JsonAnySetter
  public void setUnknownField(final String name, final Object value) {
    unknownFields.put(name, value);
  }
}
