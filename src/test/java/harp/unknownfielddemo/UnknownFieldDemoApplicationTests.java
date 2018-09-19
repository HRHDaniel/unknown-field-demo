package harp.unknownfielddemo;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import com.fasterxml.jackson.databind.ObjectMapper;
import harp.unknownfielddemo.repository.TestObject;
import harp.unknownfielddemo.repository.TestObjectRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UnknownFieldDemoApplicationTests {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private TestObjectRepository repo;

  @Test
  public void testObjectMapperHandlesUnknowns() throws Exception {

    String jsonInput = "{\"id\":\"1234\",\"knownField\":\"bing\",\"peek\":\"a boo\"}";

    ObjectMapper om = new ObjectMapper();

    TestObject obj = om.readValue(jsonInput, TestObject.class);

    // Even though it's not a field we know about, we can see that it still mapped the value
    assertThat(obj.getUnknownFields().get("peek"), is("a boo"));

    // If we update the document, then map it back to a json string, we still want the fields we didn't
    // know about to be passed along
    obj.setKnownField("updated");

    String jsonOutput = om.writeValueAsString(obj);

    assertThat(jsonOutput, containsString("updated"));
    assertThat(jsonOutput, not(containsString("bing"))); // Previous value of knownField isn't present
    // Ensure that the "unknown" field is still present
    assertThat(jsonOutput, CoreMatchers.containsString("peek"));
    assertThat(jsonOutput, CoreMatchers.containsString("a boo"));
  }

  @Test
  public void testSpringDataHandlesUnknownFields() throws Exception {
    // Setup: Insert a document that has an additional field not directly mapped to our Java class...
    String id = "4321";
    insertTestDataViaGenericMapAndMongoTemplate(id);

    // Find that document via spring data repo
    TestObject testObject = repo.findById(id).get();

    assertThat(testObject, notNullValue());
    // Verify that the field we have mapped is present and correct
    assertThat(testObject.getKnownField(), is("bing"));
    // Verify that our "unknown" field is present
    assertTrue("Expected unknown fields to contain 'peek'", testObject.getUnknownFields().containsKey("peek"));
    assertThat(testObject.getUnknownFields().get("peek"), is("a boo"));

    // If that works, I'd want to test updating a known field and storing back to mongo, hoping to not
    // lose the other fields... but didn't write that test as I know it can't save a field it has
    // already forgotten about
  }

  private void insertTestDataViaGenericMapAndMongoTemplate(String id) {
    Map<String, Object> genericObject = new HashMap<>();
    genericObject.put("_id", id);
    genericObject.put("knownField", "bing");
    genericObject.put("peek", "a boo"); // A field not on our object

    mongoTemplate.insert(genericObject, "testObject");
  }
}
