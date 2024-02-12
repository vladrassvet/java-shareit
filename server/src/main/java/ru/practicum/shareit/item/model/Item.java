package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.dto.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User owner;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private ItemRequest request;

}
