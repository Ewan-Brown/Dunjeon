package com.ewan.meworking.data;

import com.ewan.meworking.data.client.ClientAction;
import lombok.Data;

import java.util.List;

@Data
public class ClientData{
    final List<ClientAction> someKindOfInformation;
};