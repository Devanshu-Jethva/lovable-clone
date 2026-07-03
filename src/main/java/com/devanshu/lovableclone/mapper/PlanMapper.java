package com.devanshu.lovableclone.mapper;

import com.devanshu.lovableclone.dto.plan.PlanResponseListDTO;
import com.devanshu.lovableclone.entity.Plan;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanMapper {

    PlanResponseListDTO toDto(Plan plan);

    List<PlanResponseListDTO> toDtos(List<Plan> plans);
}
