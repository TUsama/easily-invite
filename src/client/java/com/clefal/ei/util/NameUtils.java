package com.clefal.ei.util;

import com.wynntils.core.text.StyledText;
import com.wynntils.core.text.StyledTextPart;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.mc.StyledTextUtils;
import net.minecraft.text.HoverEvent;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameUtils {

    public static final Pattern END_OF_HEADER_PATTERN = Pattern.compile("(.*):\\s?");

    public static Optional<StyledText> getMessageSender(StyledText match) {
        AtomicReference<Optional<StyledText>> result = new AtomicReference<>();
        Matcher matcher = match.getMatcher(END_OF_HEADER_PATTERN);
        if (matcher.find()){
            result.set(Optional.of(StyledText.fromString(matcher.group(1))));
        }
        Iterator<StyledTextPart> iterator = match.iterator();
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
                        result.set(Optional.of(StyledText.fromString(nicknameMatcher.group("username"))));
                    }
                }
            }
        }

        return result.get();
    }
}
