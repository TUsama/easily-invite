package com.clefal.ei.util;

import com.google.common.collect.ImmutableSet;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.core.text.StyledTextPart;
import com.wynntils.utils.mc.StyledTextUtils;
import net.minecraft.text.HoverEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.text.BreakIterator;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static final Pattern END_OF_HEADER_PATTERN = Pattern.compile("(.*):\\s?");
    private static final Pattern MINECRAFT_USERNAME_REGEX = Pattern.compile("[a-zA-Z0-9_]{2,16}");
    private static final Set<String> exceptionStrings = ImmutableSet.of(
            "_"
    );



    public static Pair<String, String> extractMessage(StyledText input) throws RuntimeException {
        BreakIterator bi = BreakIterator.getWordInstance(Locale.US);
        String nickName = "";


        //try to get the nick name.
        Iterator<StyledTextPart> iterator = input.iterator();
        while (iterator.hasNext()) {
            StyledTextPart next = iterator.next();
            HoverEvent hoverEvent = next.getPartStyle().getStyle().getHoverEvent();
            if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
                StyledText[] partTexts = StyledText.fromComponent(hoverEvent.getValue(HoverEvent.Action.SHOW_TEXT)).split("\n");
                StyledText[] var8 = partTexts;
                int var9 = partTexts.length;

                for (int var10 = 0; var10 < var9; ++var10) {
                    StyledText partText = var8[var10];
                    Matcher nicknameMatcher = partText.getMatcher(StyledTextUtils.NICKNAME_PATTERN);
                    if (nicknameMatcher.matches()) {
                        nickName = nicknameMatcher.group("username");
                    }
                }
            }
        }

        Pair<String, String> nameAndContent = extractNameAndContent(input);
        //Pair<String, String> test = extractNameAndContent(input.replaceFirst("Clefal", "Arclass_"));
        String name = nameAndContent.getLeft();
        String content = nameAndContent.getRight();

        //System.out.println("test is " + test);

        boolean b = MINECRAFT_USERNAME_REGEX.matcher(name).find();

        if (!b) {

            name = "";
            throw new RuntimeException("unexpected name: " + name);
        }

        return Pair.of(nickName.isBlank() ? name.strip() : nickName.strip(), content.strip());

    }

    private static Pair<String, String> extractNameAndContent(StyledText input) {
        BreakIterator bi = BreakIterator.getWordInstance(Locale.US);

        String name = "";
        String content = "";

        List<String> words = new LinkedList<>();
        String nonStyleText = input.getString(PartStyle.StyleType.NONE);
        bi.setText(nonStyleText);
        int start = bi.first();
        for (int end = bi.next();
             end != BreakIterator.DONE;
             start = end, end = bi.next()) {
            String temp = nonStyleText.substring(start, end);

            if (temp.equals(":")) {
                String last = words.getLast();
                if (exceptionStrings.contains(last)){
                    String s = words.get(words.size() - 2);
                    name = (s + last);
                } else {
                    name = last;
                }
                content = nonStyleText.substring(bi.current());
                break;
            } else {
                words.add(temp);
            }
        }

        //System.out.println("the input is" + nonStyleText + ", and the list is " + words);
        return Pair.of(name.replace(",", ""), content);
    }

    enum Word {
        None,
        Color,
    }
}
