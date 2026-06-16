package net.openan.a2at.sdk.negotiation.runtime;

import java.util.Map;
import net.openan.a2at.sdk.negotiation.types.model.NegotiationContext;
import net.openan.a2at.sdk.negotiation.types.model.NegotiationStatus;
import net.openan.a2at.sdk.negotiation.types.model.NegotiationType;

/**
 * Shared facade for negotiation orchestration.
 *
 * @since 2026-06
 */
public interface NegotiationHandlerFacade {

    /**
     * Starts a new negotiation payload for the requested role and negotiation type.
     *
     * @param role local role that is starting the negotiation
     * @param type negotiation type to initiate
     * @param contentText human-readable negotiation message
     * @param facts structured facts to include in the payload
     * @return transport payload for the initial turn
     */
    Map<String, Object> start(
            NegotiationRole role, NegotiationType type, String contentText, Map<String, Object> facts);

    /**
     * Processes a received negotiation message.
     *
     * @param message received negotiation message
     * @param context transport context payload
     * @return normalized receive payload
     */
    Map<String, Object> receive(String message, Map<String, Object> context);

    /**
     * Builds the next negotiation payload from a local context snapshot.
     *
     * @param context current negotiation context
     * @param status next negotiation status to emit
     * @param contentText message content for the next turn
     * @return transport payload for the next turn
     */
    Map<String, Object> continueNegotiation(NegotiationContext context, NegotiationStatus status, String contentText);
}
