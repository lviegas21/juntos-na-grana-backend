package com.noxius.juntosnagrana.web.rest;

import com.noxius.juntosnagrana.domain.enumeration.GoalCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador temporário para testar a deserialização de enums.
 * Remova após resolver o problema da categoria.
 */
@RestController
@RequestMapping("/api/test")
public class TestEnumController {

    private final Logger log = LoggerFactory.getLogger(TestEnumController.class);

    /**
     * {@code POST /api/test/enum} : Teste de deserialização de enum.
     *
     * @param request objeto com categoria para teste
     * @return a categoria recebida
     */
    @PostMapping("/enum")
    public ResponseEntity<TestResponse> testEnum(@RequestBody TestRequest request) {
        log.debug("REST request to test enum deserialization: {}", request);
        
        // Criar resposta com a mesma categoria recebida
        TestResponse response = new TestResponse();
        response.setCategory(request.getCategory());
        response.setReceivedValue(request.getCategory().name());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Classe para receber a categoria no teste.
     */
    public static class TestRequest {
        private GoalCategory category;

        public GoalCategory getCategory() {
            return category;
        }

        public void setCategory(GoalCategory category) {
            this.category = category;
        }

        @Override
        public String toString() {
            return "TestRequest{" +
                "category=" + category +
                '}';
        }
    }

    /**
     * Classe para retornar a categoria no teste.
     */
    public static class TestResponse {
        private GoalCategory category;
        private String receivedValue;

        public GoalCategory getCategory() {
            return category;
        }

        public void setCategory(GoalCategory category) {
            this.category = category;
        }

        public String getReceivedValue() {
            return receivedValue;
        }

        public void setReceivedValue(String receivedValue) {
            this.receivedValue = receivedValue;
        }
    }
}
