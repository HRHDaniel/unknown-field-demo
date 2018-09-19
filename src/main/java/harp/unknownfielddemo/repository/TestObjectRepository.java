package harp.unknownfielddemo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestObjectRepository extends MongoRepository<TestObject, String> {

}
