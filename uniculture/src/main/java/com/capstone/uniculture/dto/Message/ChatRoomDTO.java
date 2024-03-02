package com.capstone.uniculture.dto.Message;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.UUID;

@Data
public class ChatRoomDTO {
  private long id;
  private String name;
  private String nickname;
  private String lastestMessage;
  private int memberCount;
}
