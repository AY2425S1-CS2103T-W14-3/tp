package hallpointer.address.model.member;

import static hallpointer.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_ROOM_BOB;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static hallpointer.address.logic.commands.CommandTestUtil.VALID_TELEGRAM_BOB;
import static hallpointer.address.testutil.Assert.assertThrows;
import static hallpointer.address.testutil.TypicalMembers.ALICE;
import static hallpointer.address.testutil.TypicalMembers.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import hallpointer.address.model.point.Point;
import hallpointer.address.model.session.Session;
import hallpointer.address.model.session.SessionDate;
import hallpointer.address.model.session.SessionName;
import hallpointer.address.testutil.MemberBuilder;

public class MemberTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Member member = new MemberBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> member.getTags().remove(0));
    }

    @Test
    public void isSameMember() {
        // same object -> returns true
        assertTrue(ALICE.isSameMember(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSameMember(null));

        // same name, all other attributes different -> returns true
        Member editedAlice = new MemberBuilder(ALICE).withTelegram(VALID_TELEGRAM_BOB)
                .withRoom(VALID_ROOM_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSameMember(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new MemberBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSameMember(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Member editedBob = new MemberBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSameMember(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new MemberBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSameMember(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Member aliceCopy = new MemberBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different member -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Member editedAlice = new MemberBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different telegram -> returns false
        editedAlice = new MemberBuilder(ALICE).withTelegram(VALID_TELEGRAM_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different room -> returns false
        editedAlice = new MemberBuilder(ALICE).withRoom(VALID_ROOM_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new MemberBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void addPoints_validPoints_increasesTotalPoints() {
        Member member = new MemberBuilder(ALICE).build();
        Point pointsToAdd = new Point(10);
        member.addPoints(pointsToAdd);
        assertEquals(new Point(10), member.getTotalPoints());
    }

    @Test
    public void addSession_validSession_increasesSessions() {
        Member member = new MemberBuilder(ALICE).build();
        Session newSession = new Session(new SessionName(VALID_NAME_BOB),
                                new SessionDate("16 Oct 2024"), new Point(10));
        member.addSession(newSession);
        assertTrue(member.getSessions().contains(newSession));
        assertEquals(newSession.getPoints(), member.getTotalPoints());
    }

    @Test
    public void removeSession_validSession_decreasesSessions() {
        Member member = new MemberBuilder(ALICE).build();
        Session newSession = new Session(new SessionName(VALID_NAME_BOB),
                                new SessionDate("10 Nov 2025"), new Point(10));
        member.addSession(newSession);
        member.removeSession(newSession);
        assertFalse(member.getSessions().contains(newSession));
        assertEquals(new Point(0), member.getTotalPoints());
    }

    @Test
    public void addSession_nullSession_throwsNullPointerException() {
        Member member = new MemberBuilder(ALICE).build();
        assertThrows(NullPointerException.class, () -> member.addSession(null));
    }

    @Test
    public void removeSession_nullSession_throwsNullPointerException() {
        Member member = new MemberBuilder(ALICE).build();
        assertThrows(NullPointerException.class, () -> member.removeSession(null));
    }

    @Test
    public void toStringMethod() {
        String expected = Member.class.getCanonicalName() + "{name=" + ALICE.getName()
            + ", telegram=" + ALICE.getTelegram() + ", room=" + ALICE.getRoom()
            + ", tags=" + ALICE.getTags() + ", totalPoints=" + ALICE.getTotalPoints()
            + ", sessions=" + ALICE.getSessions() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
