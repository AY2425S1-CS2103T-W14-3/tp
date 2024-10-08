package hallpointer.address.logic.parser;

import static hallpointer.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static hallpointer.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static hallpointer.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static hallpointer.address.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static hallpointer.address.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static hallpointer.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static hallpointer.address.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static hallpointer.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static hallpointer.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static hallpointer.address.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static hallpointer.address.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static hallpointer.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static hallpointer.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static hallpointer.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static hallpointer.address.logic.parser.CliSyntax.PREFIX_TAG;
import static hallpointer.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static hallpointer.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static hallpointer.address.testutil.TypicalIndexes.INDEX_FIRST_MEMBER;
import static hallpointer.address.testutil.TypicalIndexes.INDEX_SECOND_MEMBER;
import static hallpointer.address.testutil.TypicalIndexes.INDEX_THIRD_MEMBER;

import org.junit.jupiter.api.Test;

import hallpointer.address.commons.core.index.Index;
import hallpointer.address.logic.Messages;
import hallpointer.address.logic.commands.EditCommand;
import hallpointer.address.logic.commands.EditCommand.EditMemberDescriptor;
import hallpointer.address.model.member.Email;
import hallpointer.address.model.member.Name;
import hallpointer.address.model.member.Phone;
import hallpointer.address.model.member.Room;
import hallpointer.address.model.tag.Tag;
import hallpointer.address.testutil.EditMemberDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Room.MESSAGE_CONSTRAINTS); // invalid room
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Member} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_MEMBER;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withRoom(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_MEMBER;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_MEMBER;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditMemberDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditMemberDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // room
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AMY;
        descriptor = new EditMemberDescriptorBuilder().withRoom(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditMemberDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_MEMBER;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        Index targetIndex = INDEX_THIRD_MEMBER;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        EditMemberDescriptor descriptor = new EditMemberDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}