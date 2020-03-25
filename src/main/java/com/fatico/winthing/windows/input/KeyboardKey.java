package com.fatico.winthing.windows.input;

import com.google.common.collect.ImmutableSet;
import com.sun.jna.platform.win32.WinDef;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum KeyboardKey {

    CANCEL(3),
    BACK(8, "BACKSPACE"),
    TAB(9),
    CLEAR(12),
    RETURN(13, "ENTER"),
    SHIFT(16),
    CONTROL(17, "CTRL"),
    MENU(18, "ALT"),
    PAUSE(19),
    CAPITAL(20, "CAPSLOCK"),
    KANA(0x15),
    HANGEUL(0x15),
    HANGUL(0x15),
    JUNJA(0x17),
    FINAL(0x18),
    HANJA(0x19),
    KANJI(0x19),
    ESCAPE(0x1B, "ESC"),
    CONVERT(0x1C),
    NONCONVERT(0x1D),
    ACCEPT(0x1E),
    MODECHANGE(0x1F),
    SPACE(32, " "),
    PRIOR(33),
    NEXT(34),
    END(35),
    HOME(36),
    LEFT(37),
    UP(38),
    RIGHT(39),
    DOWN(40),
    SELECT(41),
    PRINT(42),
    EXECUTE(43),
    SNAPSHOT(44),
    INSERT(45),
    DELETE(46),
    HELP(47),

    NUM0(0x30, "0"),
    NUM1(0x31, "1"),
    NUM2(0x32, "2"),
    NUM3(0x33, "3"),
    NUM4(0x34, "4"),
    NUM5(0x35, "5"),
    NUM6(0x36, "6"),
    NUM7(0x37, "7"),
    NUM8(0x38, "8"),
    NUM9(0x39, "9"),

    A(0x41),
    B(0x42),
    C(0x43),
    D(0x44),
    E(0x45),
    F(0x46),
    G(0x47),
    H(0x48),
    I(0x49),
    J(0x4a),
    K(0x4b),
    L(0x4c),
    M(0x4d),
    N(0x4e),
    O(0x4f),
    P(0x50),
    Q(0x51),
    R(0x52),
    S(0x53),
    T(0x54),
    U(0x55),
    V(0x56),
    W(0x57),
    X(0x58),
    Y(0x59),
    Z(0x5a),

    LWIN(0x5B, "LEFT_WIN", "LEFT_WINDOWS"),
    RWIN(0x5C, "RIGHT_WIN", "RIGHT_WINDOWS"),
    APPS(0x5D),
    SLEEP(0x5F),
    NUMPAD0(0x60),
    NUMPAD1(0x61),
    NUMPAD2(0x62),
    NUMPAD3(0x63),
    NUMPAD4(0x64),
    NUMPAD5(0x65),
    NUMPAD6(0x66),
    NUMPAD7(0x67),
    NUMPAD8(0x68),
    NUMPAD9(0x69),
    MULTIPLY(0x6A),
    ADD(0x6B),
    SEPARATOR(0x6C),
    SUBTRACT(0x6D),
    DECIMAL(0x6E),
    DIVIDE(0x6F),
    F1(0x70),
    F2(0x71),
    F3(0x72),
    F4(0x73),
    F5(0x74),
    F6(0x75),
    F7(0x76),
    F8(0x77),
    F9(0x78),
    F10(0x79),
    F11(0x7A),
    F12(0x7B),
    F13(0x7C),
    F14(0x7D),
    F15(0x7E),
    F16(0x7F),
    F17(0x80),
    F18(0x81),
    F19(0x82),
    F20(0x83),
    F21(0x84),
    F22(0x85),
    F23(0x86),
    F24(0x87),
    NUMLOCK(0x90),
    SCROLL(0x91),
    LSHIFT(0xA0),
    RSHIFT(0xA1),
    LCONTROL(0xA2),
    RCONTROL(0xA3),
    LMENU(0xA4, "LEFT_ALT"),
    RMENU(0xA5, "RIGHT_ALT"),
    BROWSER_BACK(0xA6),
    BROWSER_FORWARD(0xA7),
    BROWSER_REFRESH(0xA8),
    BROWSER_STOP(0xA9),
    BROWSER_SEARCH(0xAA),
    BROWSER_FAVORITES(0xAB),
    BROWSER_HOME(0xAC),
    VOLUME_MUTE(0xAD),
    VOLUME_DOWN(0xAE),
    VOLUME_UP(0xAF),
    MEDIA_NEXT_TRACK(0xB0),
    MEDIA_PREV_TRACK(0xB1),
    MEDIA_STOP(0xB2),
    MEDIA_PLAY_PAUSE(0xB3),
    LAUNCH_MAIL(0xB4),
    LAUNCH_MEDIA_SELECT(0xB5),
    LAUNCH_APP1(0xB6),
    LAUNCH_APP2(0xB7),
    OEM_1(0xBA),
    OEM_PLUS(0xBB),
    OEM_COMMA(0xBC),
    OEM_MINUS(0xBD),
    OEM_PERIOD(0xBE),
    OEM_2(0xBF),
    OEM_3(0xC0),
    OEM_4(0xDB),
    OEM_5(0xDC),
    OEM_6(0xDD),
    OEM_7(0xDE),
    OEM_8(0xDF),
    OEM_102(0xE2),
    PROCESSKEY(0xE5),
    PACKET(0xE7),
    ATTN(0xF6),
    CRSEL(0xF7),
    EXSEL(0xF8),
    EREOF(0xF9),
    PLAY(0xFA),
    ZOOM(0xFB),
    NONAME(0xFC),
    PA1(0xFD),
    OEM_CLEAR(0xFE);

    private static final Map<String, KeyboardKey> index = new HashMap<>();
    static {
        for (final KeyboardKey key : KeyboardKey.values()) {
            index.put(key.name().toLowerCase(), key);
            for (final String alias : key.aliases) {
                index.put(alias.toLowerCase(), key);
            }
        }
    }

    private final WinDef.WORD virtualKeyCode;
    private final ImmutableSet<String> aliases;

    KeyboardKey(final int virtualKeyCode, final String... aliases) {
        assert 0 < virtualKeyCode;
        assert virtualKeyCode < 0xFF;
        this.virtualKeyCode = new WinDef.WORD(virtualKeyCode);
        this.aliases = ImmutableSet.copyOf(aliases);
    }

    public WinDef.WORD getVirtualKeyCode() {
        return virtualKeyCode;
    }

    public static KeyboardKey getByCodename(final String codename) {
        final KeyboardKey key = index.get(codename.toLowerCase());
        if (key == null) {
            throw new NoSuchElementException("Unknown key: " + codename);
        }
        return key;
    }

}
