package hallpointer.address.storage;

import static hallpointer.address.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import hallpointer.address.commons.exceptions.IllegalValueException;
import hallpointer.address.commons.util.JsonUtil;
import hallpointer.address.model.AddressBook;
import hallpointer.address.testutil.TypicalMembers;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_MEMBERS_FILE = TEST_DATA_FOLDER.resolve("typicalMembersAddressBook.json");
    private static final Path INVALID_MEMBER_FILE = TEST_DATA_FOLDER.resolve("invalidMemberAddressBook.json");
    private static final Path DUPLICATE_MEMBER_FILE = TEST_DATA_FOLDER.resolve("duplicateMemberAddressBook.json");

    @Test
    public void toModelType_typicalMembersFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_MEMBERS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalMembersAddressBook = TypicalMembers.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalMembersAddressBook);
    }

    @Test
    public void toModelType_invalidMemberFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_MEMBER_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateMembers_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_MEMBER_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_MEMBER,
                dataFromFile::toModelType);
    }

}