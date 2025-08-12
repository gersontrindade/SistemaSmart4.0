package com.tecdes.sistema_bancada.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Bloco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int cor;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    @JsonBackReference
    private Pedido pedido;

    @OneToMany(mappedBy = "bloco", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Lamina> laminas;

    public Bloco() {
        this.laminas = new ArrayList<>();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Lamina> getLaminas() {
        return laminas;
    }

    public void setLaminas(List<Lamina> laminas) {
        this.laminas = laminas;
        if (laminas != null) {
            for (Lamina lamina : laminas) {
                lamina.setBloco(this);
            }
        }
    }
}

