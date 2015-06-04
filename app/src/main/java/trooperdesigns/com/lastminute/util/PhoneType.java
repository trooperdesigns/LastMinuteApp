package trooperdesigns.com.lastminute.util;

public enum PhoneType {

    TYPE_HOME(1),
    TYPE_MOBILE(2),
    TYPE_WORK(3),
    TYPE_FAX_WORK(4),
    TYPE_FAX_HOME(5),
    TYPE_PAGER(6),
    TYPE_OTHER(7),
    TYPE_CALLBACK(8),
    TYPE_CAR(9),
    TYPE_COMPANY_MAIN(10),
    TYPE_ISDN(11),
    TYPE_MAIN(12),
    TYPE_OTHER_FAX(13),
    TYPE_RADIO(14),
    TYPE_TELEX(15),
    TYPE_TTY_TDD(16);

    final int value;

    PhoneType(final int newValue) {
        value = newValue;
    }

    public String getType(int value){
        switch(value){
            case(1):
                return "HOME";
            case(2):
                return "MOBILE";
            case(3):
                return "WORK";
            case(4):
                return "FAX WORK";
            case(5):
                return "FAX HOME";
            case(6):
                return "PAGER";
            case(7):
                return "OTHER";
            case(8):
                return "CALLBACK";
            case(9):
                return "COMPANY MAIN";
            default:
                return "";
        }
    }

}
