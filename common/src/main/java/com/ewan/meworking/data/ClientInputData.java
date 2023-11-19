package com.ewan.meworking.data;

import com.ewan.meworking.data.client.UserInput;
import lombok.Data;

import java.util.List;

public record ClientInputData(List<UserInput> inputs) {
};