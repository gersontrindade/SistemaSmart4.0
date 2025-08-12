package com.tecdes.sistema_bancada.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecdes.sistema_bancada.dto.BlocoDTO;
import com.tecdes.sistema_bancada.dto.LaminaDTO;
import com.tecdes.sistema_bancada.dto.PedidoDTO;
import com.tecdes.sistema_bancada.model.Bloco;
import com.tecdes.sistema_bancada.model.Lamina;
import com.tecdes.sistema_bancada.model.Pedido;
import com.tecdes.sistema_bancada.repository.PedidoRepository;

@Controller
public class LojaController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @PostMapping("/salvar-pedidos")
    @ResponseBody
    public ResponseEntity<Long> receberPedido(@RequestBody PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setTipo(pedidoDTO.getTipo());
        pedido.setStatusOrderProduction(pedidoDTO.getStatusOrderProduction());
        pedido.setTimeStamp(pedidoDTO.getTimeStamp());

        List<Bloco> blocos = new ArrayList<>();

        for (BlocoDTO blocoDTO : pedidoDTO.getBlocos()) {
            Bloco bloco = new Bloco();
            bloco.setCor(blocoDTO.getCorBloco());
            bloco.setPedido(pedido);

            List<Lamina> laminas = new ArrayList<>();
            for (LaminaDTO laminaDTO : blocoDTO.getLaminas()) {
                Lamina lamina = new Lamina();
                lamina.setCor(laminaDTO.getCor());
                lamina.setPadrao(laminaDTO.getPadrao());
                lamina.setBloco(bloco);
                laminas.add(lamina);
            }

            bloco.setLaminas(laminas);
            blocos.add(bloco);
        }

        pedido.setBlocos(blocos);

        // 1ª persistência: gera o ID
        pedido = pedidoRepository.save(pedido);

        // 2ª etapa: define orderProduction com o ID
        pedido.setOrderProduction(pedido.getId().intValue());

        // 2ª persistência: atualiza com orderProduction preenchido
        pedidoRepository.save(pedido);

        // 🔽 Imprimir JSON no console
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonPedido = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(pedido);
            System.out.println("🔎 Pedido recebido:");
            System.out.println(jsonPedido);
        } catch (JsonProcessingException e) {
            System.out.println("Erro ao converter pedido para JSON: " + e.getMessage());
        }

        return ResponseEntity.ok(pedido.getId());
    }

    @GetMapping("/listar-pedidos")
    @ResponseBody
    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    @DeleteMapping("/api/pedidos/{id}")
    @ResponseBody
    public String excluirPedido(@PathVariable Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        if (pedido.isPresent()) {
            pedidoRepository.deleteById(id);
            return "DELETADO";
        }
        return "NAO ENCONTRADO";
    }

    @GetMapping("/listar-pedido/{id}")
    @ResponseBody
    public ResponseEntity<Pedido> buscarPedidoPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/salvar-pedido/status")
    @ResponseBody
    public ResponseEntity<String> atualizarStatusPedido(@RequestBody PedidoDTO dto) {
        if (dto.getId() == null) {
            return ResponseEntity.badRequest().body("ID do pedido não informado.");
        }

        Optional<Pedido> pedidoOptional = pedidoRepository.findById(dto.getId());

        if (pedidoOptional.isPresent()) {
            Pedido pedido = pedidoOptional.get();
            pedido.setStatusOrderProduction(dto.getStatusOrderProduction());
            pedidoRepository.save(pedido);
            return ResponseEntity.ok("Status atualizado com sucesso.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Pedido com ID " + dto.getId() + " não encontrado.");
        }
    }

}
