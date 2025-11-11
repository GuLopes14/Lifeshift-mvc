package br.com.lifeshift.lifeshift.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

        Integer statusCode = null;
        if (status != null) {
            statusCode = Integer.valueOf(status.toString());
        }

        model.addAttribute("status", statusCode);

        // Mensagem amigável baseada no código de status
        String errorMessage = "Ocorreu um erro inesperado.";
        if (statusCode != null) {
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "Página não encontrada.";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Acesso negado.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Erro interno no servidor.";
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                errorMessage = "Requisição inválida.";
            }
        }

        // Se houver uma mensagem de erro específica, usa ela
        if (message != null && !message.toString().isEmpty()) {
            model.addAttribute("message", message.toString());
        } else {
            model.addAttribute("message", errorMessage);
        }

        // Log para debug
        System.err.println("Error occurred: Status=" + statusCode + ", Message=" + message);
        if (exception != null) {
            System.err.println("Exception: " + exception);
        }

        return "error";
    }
}

