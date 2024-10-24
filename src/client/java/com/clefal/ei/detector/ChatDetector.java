package com.clefal.ei.detector;

import com.clefal.ei.EasilyInvite;
import com.clefal.ei.util.NameUtils;
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
import net.neoforged.bus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.clefal.ei.util.NameUtils.END_OF_HEADER_PATTERN;
import static net.minecraft.text.ClickEvent.Action.RUN_COMMAND;

public class ChatDetector {

    private final static String format = "#(.+?)#";

    protected static boolean shouldDetect = false;

    protected static String triggerString = "";


    @SubscribeEvent
    public void onSpecialChatReceived(ChatMessageReceivedEvent event) {
        if (event.getRecipientType() == RecipientType.INFO) return;
        var message = event.getStyledText();
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = message.getMatcher(pattern);
        if (!matcher.find()) return;
        EasilyInvite.LOGGER.warn("1");
        EasilyInvite.LOGGER.warn("the name is: " + NameUtils.getMessageSender(message).orElse(StyledText.EMPTY).getString());
        String thisPlayerName = McUtils.playerName();
        if (thisPlayerName.equals(NameUtils.getMessageSender(message).orElse(StyledText.EMPTY).getString())) {
            //shouldDetect = true;

            EasilyInvite.LOGGER.warn("2");

            StyledText modified = message.iterateBackwards((currentPart, mutateTo) -> {
                if (END_OF_HEADER_PATTERN.matcher(currentPart.getString(null, PartStyle.StyleType.NONE)).matches()) {
                    EasilyInvite.LOGGER.warn("break");
                    return IterationDecision.BREAK;
                } else {
                    EasilyInvite.LOGGER.warn("scanning...");

                    StyledTextPart partToReplace = currentPart;
                    String lastPart;
                    for (Matcher matcher1 = pattern.matcher(partToReplace.getString((PartStyle) null, PartStyle.StyleType.NONE)); matcher.find(); matcher1 = pattern.matcher(lastPart)) {
                        String match = partToReplace.getString((PartStyle) null, PartStyle.StyleType.NONE);
                        String firstPart = match.substring(0, matcher1.start());
                        String mentionPart = match.substring(matcher1.start(), matcher1.end());
                        lastPart = match.substring(matcher1.end());
                        PartStyle partStyle = partToReplace.getPartStyle();
                        StyledTextPart first = new StyledTextPart(firstPart, partStyle.getStyle(), (StyledText) null, Style.EMPTY);
                        StyledTextPart mention = new StyledTextPart(mentionPart, partStyle.getStyle().withColor(ColorChatFormatting.YELLOW.getChatFormatting()), (StyledText) null, first.getPartStyle().getStyle());
                        StyledTextPart last = new StyledTextPart(lastPart, partStyle.getStyle(), (StyledText) null, Style.EMPTY);
                        mutateTo.remove(partToReplace);
                        mutateTo.add(first);
                        mutateTo.add(mention);
                        mutateTo.add(last);
                        partToReplace = last;
                        //triggerString = mentionPart;
                    }

                    return IterationDecision.CONTINUE;
                }
            });
            event.setMessage(modified);
        }
    }

    @SubscribeEvent
    public void onReceiveResponse(ChatMessageReceivedEvent event) {
        if (event.getRecipientType() == RecipientType.INFO) return;
        if (!shouldDetect) return;
        if (ChatDetector.triggerString.isBlank()){
            ChatDetector.shouldDetect = false;
            throw new RuntimeException("detection mark is true but the trigger string is blank!");
        }
        StyledText styledText = event.getStyledText();
        if (styledText.contains(triggerString)){
            var newText = styledText;
            newText.append(StyledText.fromComponent(MutableText.of((PlainTextContent) () -> "(invite)").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(RUN_COMMAND, "/party invite " + NameUtils.getMessageSender(styledText))))));
        }
    }

}
