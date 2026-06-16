package net.openan.a2at.sdk.negotiation.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.LinkedHashMap;
import java.util.Map;
import net.openan.a2at.sdk.negotiation.types.model.NegotiationContext;
import net.openan.a2at.sdk.negotiation.types.model.NegotiationStatus;
import net.openan.a2at.sdk.negotiation.types.model.NegotiationType;
import org.junit.jupiter.api.Test;

class RoleBoundNegotiationOrchestratorTest {

    @Test
    void delegatesAllOperationsToSharedHandlerWithBoundRole() {
        FakeNegotiationHandler handler = new FakeNegotiationHandler();
        RoleBoundNegotiationOrchestrator orchestrator =
                new RoleBoundNegotiationOrchestrator(handler, NegotiationRole.CLIENT);

        Map<String, Object> facts = Map.of("site", "A");
        Map<String, Object> contextMap = new LinkedHashMap<>();
        contextMap.put("negotiationType", "clarification");
        contextMap.put("negotiationId", "neg-1");
        contextMap.put("round", 1);
        contextMap.put("status", "in-progress");
        NegotiationContext context =
                new NegotiationContext(NegotiationType.CLARIFICATION, "neg-1", 1, NegotiationStatus.IN_PROGRESS);

        Map<String, Object> startResult =
                orchestrator.startNegotiation(NegotiationType.CLARIFICATION, "Please clarify the target.", facts);
        Map<String, Object> receiveResult = orchestrator.receiveNegotiation("Clarify the site", contextMap);
        Map<String, Object> continueResult =
                orchestrator.continueNegotiation(context, NegotiationStatus.IN_PROGRESS, "Site A");

        assertSame(handler.startResult, startResult);
        assertSame(handler.receiveResult, receiveResult);
        assertSame(handler.continueResult, continueResult);
        assertEquals(NegotiationRole.CLIENT, handler.startRole);
        assertEquals(NegotiationType.CLARIFICATION, handler.startType);
        assertEquals("Please clarify the target.", handler.startContentText);
        assertSame(facts, handler.startFacts);
        assertEquals("Clarify the site", handler.receiveMessage);
        assertSame(contextMap, handler.receiveContext);
        assertSame(context, handler.continueContext);
        assertEquals(NegotiationStatus.IN_PROGRESS, handler.continueStatus);
        assertEquals("Site A", handler.continueContentText);
    }

    private static final class FakeNegotiationHandler implements NegotiationHandlerFacade {
        private final Map<String, Object> startResult = Map.of("started", true);
        private final Map<String, Object> receiveResult = Map.of("received", true);
        private final Map<String, Object> continueResult = Map.of("continued", true);

        private NegotiationRole startRole;
        private NegotiationType startType;
        private String startContentText;
        private Map<String, Object> startFacts;
        private String receiveMessage;
        private Map<String, Object> receiveContext;
        private NegotiationContext continueContext;
        private NegotiationStatus continueStatus;
        private String continueContentText;

        @Override
        public Map<String, Object> start(
                NegotiationRole role, NegotiationType type, String contentText, Map<String, Object> facts) {
            this.startRole = role;
            this.startType = type;
            this.startContentText = contentText;
            this.startFacts = facts;
            return startResult;
        }

        @Override
        public Map<String, Object> receive(String message, Map<String, Object> context) {
            this.receiveMessage = message;
            this.receiveContext = context;
            return receiveResult;
        }

        @Override
        public Map<String, Object> continueNegotiation(
                NegotiationContext context, NegotiationStatus status, String contentText) {
            this.continueContext = context;
            this.continueStatus = status;
            this.continueContentText = contentText;
            return continueResult;
        }
    }
}
