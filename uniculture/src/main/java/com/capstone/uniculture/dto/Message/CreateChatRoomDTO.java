package com.capstone.uniculture.dto.Message;

import lombok.Data;

import java.util.List;

@Data
public class CreateChatRoomDTO {
  private String name;
  private List<Long> memberIds;

}
