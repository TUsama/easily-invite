package com.clefal.ei.detector;

import com.clefal.ei.util.MessageUtils;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.core.text.StyledTextPart;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.RecipientType;
import com.wynntils.utils.colors.ColorChatFormatting;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.type.IterationDecision;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.neoforged.bus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.clefal.ei.util.MessageUtils.END_OF_HEADER_PATTERN;
import static net.minecraft.text.ClickEvent.Action.RUN_COMMAND;

public class ChatDetector {

    private final static String format = "(?<first>##)(?<target>.+?)(?<second>##)";

    private final static String ArrowInMsg = "\uE003";

    protected static boolean shouldDetect = false;

    protected static String triggerString = "";


    @SubscribeEvent
    public void detectPlayerTrigger(ChatMessageReceivedEvent event) {
        if (event.getRecipientType() == RecipientType.INFO) return;
        var message = event.getStyledText();

        Pattern pattern = Pattern.compile(format);
        Matcher matcher = message.getMatcher(pattern);
        if (!matcher.find()) return;
        //exclude the /msg situation
        if (message.contains(ArrowInMsg)) return;

        AtomicReference<String> mentionPart = new AtomicReference<>("");
        StyledText modified = colorizedStyledText(message, pattern, mentionPart);
        event.setMessage(modified);

        Pair<String, String> nameAndContent = MessageUtils.extractMessage(message.getString(PartStyle.StyleType.NONE));

        String thisPlayerName = McUtils.playerName();
        String name = nameAndContent.getLeft();

        if (thisPlayerName.equals(name)) {
            if (!mentionPart.get().isBlank()) {
                shouldDetect = true;
                triggerString = mentionPart.get();
            }

        }
    }

    private static @NotNull StyledText colorizedStyledText(StyledText message, Pattern pattern, AtomicReference<String> mentionPart) {
        StyledText modified = message.iterateBackwards((currentPart, mutateTo) -> {
            if (END_OF_HEADER_PATTERN.matcher(currentPart.getString(null, PartStyle.StyleType.NONE)).matches()) {

                return IterationDecision.BREAK;
            } else {
                StyledTextPart partToReplace = currentPart;
                String lastPart;

                Matcher matcher1 = pattern.matcher(partToReplace.getString((PartStyle) null, PartStyle.StyleType.NONE));
                while (matcher1.find()) {
                    String match = partToReplace.getString((PartStyle) null, PartStyle.StyleType.NONE);

                    String firstPart = match.substring(0, matcher1.start());
                    mentionPart.set(match.substring(matcher1.start(), matcher1.end()).replaceAll("#", ""));

                    lastPart = match.substring(matcher1.end());
                    PartStyle partStyle = partToReplace.getPartStyle();
                    StyledTextPart first = new StyledTextPart(firstPart, partStyle.getStyle(), (StyledText) null, Style.EMPTY);
                    StyledTextPart mention = new StyledTextPart(mentionPart.get(), partStyle.getStyle().withColor(ColorChatFormatting.YELLOW.getChatFormatting()), (StyledText) null, first.getPartStyle().getStyle());
                    StyledTextPart last = new StyledTextPart(lastPart, partStyle.getStyle(), (StyledText) null, Style.EMPTY);
                    mutateTo.remove(partToReplace);
                    mutateTo.add(first);
                    mutateTo.add(mention);
                    mutateTo.add(last);

                    partToReplace = last;

                    matcher1 = pattern.matcher(lastPart);
                }
                return IterationDecision.CONTINUE;
            }
        });
        return modified;
    }


    @SubscribeEvent
    public void ReceiveOthersResponse(ChatMessageReceivedEvent event) {
        if (event.getRecipientType() == RecipientType.INFO) return;
        StyledText styledText1 = event.getStyledText();
        if (!shouldDetect) return;
        if (ChatDetector.triggerString.isBlank()) {
            ChatDetector.shouldDetect = false;
            throw new RuntimeException("detection mark is true but the trigger string is blank!");
        }
        //exclude the /msg situation
        if (styledText1.contains(ArrowInMsg)) return;
        try {
            Pair<String, String> nameAndContent = MessageUtils.extractMessage(styledText1.getString(PartStyle.StyleType.NONE));
            if (nameAndContent.getRight().equals(McUtils.playerName())) return;

            String content = nameAndContent.getRight();

            if (content.equals(triggerString)) {

                StyledText newText = styledText1.append("  ").appendPart(new StyledTextPart("(invite)", styledText1.getLastPart().getPartStyle().getStyle().withClickEvent(new ClickEvent(RUN_COMMAND, "/party invite " + nameAndContent.getLeft())).withFormatting(Formatting.GOLD, Formatting.UNDERLINE), null, Style.EMPTY));

                System.out.println(newText);
                event.setMessage(newText);
            }
        } catch (RuntimeException ignored){
        }

    }



}
