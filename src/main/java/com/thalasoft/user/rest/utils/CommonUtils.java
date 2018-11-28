package com.thalasoft.user.rest.utils;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;

public final class CommonUtils {

  public static final Sort stripColumnsFromSorting(Sort sort, Set<String> nonSortableColumns) {
    return Sort.by(sort.stream().filter(order -> {
      return !nonSortableColumns.contains(order.getProperty());
    }).collect(Collectors.toList()));
  }

}
