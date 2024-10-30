package com.clefal.ei.util;

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


    public static Pair<String, String> extractMessage(String input) throws RuntimeException{
        BreakIterator bi = BreakIterator.getWordInstance(Locale.US);
        List<String> words = new LinkedList<>();
        bi.setText(input);
        String name = "";
        String content = "";
        int start = bi.first();
        for (int end = bi.next();
             end != BreakIterator.DONE;
             start = end, end = bi.next()) {
            String temp = input.substring(start, end);

            if (temp.equals(":")){
                name = words.getLast().replace(",", "");


                content = input.substring(bi.current());
                break;
            } else {
                words.add(temp);
            }
        }
        boolean b = MINECRAFT_USERNAME_REGEX.matcher(name).find();

        if (!b) {

            name = "";
            System.out.println(words);
            throw new RuntimeException("unexpected name: " + name);
        }

        return Pair.of(name.strip(), content.strip());
        /*
        Word currentState = Word.None;
        StringBuilder segmentBuilder = new StringBuilder();
        List<Pair<String, Word>> result = new ArrayList<>();
        for (int i = 0; i < words.size(); ++i) {
            String s = words.get(i);

            if ("&".equals(s)) {
                if (segmentBuilder.length() > 0) {
                    result.add(Pair.of(segmentBuilder.toString(), Word.None));
                    segmentBuilder.setLength(0); // 清空segmentBuilder
                }
                currentState = Word.Color;
                result.add(Pair.of(s, currentState));
                continue;
            }

            if (Word.Color.equals(currentState)) {
                if ("#".equals(s)) {
                    s = String.join("", words.subList(i, i + 9));
                    i += 8;
                    result.add(Pair.of(s, currentState));
                } else {
                    result.add(Pair.of(s, currentState));
                }
                currentState = Word.None;
                continue;
            }

            segmentBuilder.append(s);
        }

        if (segmentBuilder.length() > 0) {
            result.add(Pair.of(segmentBuilder.toString(), Word.None));
            segmentBuilder.setLength(0);
        }

        List<Pair<String, Word>> mergedResult = new ArrayList<>();
        StringBuilder colorSegmentBuilder = new StringBuilder();
        for (Pair<String, Word> pair : result) {
            if (pair.getValue() == Word.Color) {
                colorSegmentBuilder.append(pair.getKey());
            } else {
                if (colorSegmentBuilder.length() > 0) {
                    mergedResult.add(Pair.of(colorSegmentBuilder.toString(), Word.Color));
                    colorSegmentBuilder.setLength(0); // Clear colorSegmentBuilder
                }
                mergedResult.add(pair);
            }
        }
        if (colorSegmentBuilder.length() > 0) {
            mergedResult.add(Pair.of(colorSegmentBuilder.toString(), Word.Color));
        }

        String username = null;
        int usernameIndex = -1;
        for (int i = 0; i < mergedResult.size(); i++) {
            Pair<String, Word> pair = mergedResult.get(i);
            if (pair.getValue() == Word.None && pair.getKey().endsWith(": ")) {
                username = pair.getKey().substring(0, pair.getKey().length() - 2);
                usernameIndex = i;
                break;
            }
        }

        String message = null;
        if (username != null) {
            StringBuilder messageBuilder = new StringBuilder();
            for (int i = usernameIndex + 1; i < mergedResult.size(); i++) {
                Pair<String, Word> pair = mergedResult.get(i);
                if (pair.getValue() == Word.None && !pair.getKey().equals(username)) {
                    messageBuilder.append(pair.getKey());
                }
            }
            message = messageBuilder.toString();
        }*/
        // Here you can convert mergedResult to the desired output format
        //return Pair.of(username, message);
    }

    enum Word {
        None,
        Color,
    }
}
