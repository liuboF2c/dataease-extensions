package io.dataease.plugins.view.official.handler;

import io.dataease.plugins.common.dto.chart.AxisChartDataAntVDTO;
import io.dataease.plugins.common.dto.chart.ChartDimensionDTO;
import io.dataease.plugins.common.dto.chart.ChartQuotaDTO;
import io.dataease.plugins.view.entity.*;
import io.dataease.plugins.view.handler.PluginViewRSHandler;
import io.dataease.plugins.view.official.dto.SymbolMapResultDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SymbolMapRSHandler implements PluginViewRSHandler<Map> {

    @Override
    public Map format(PluginViewParam pluginViewParam, List<String[]> data, boolean isDrill) {
        List<PluginViewField> xAxis = new ArrayList<>();
        List<PluginViewField> yAxis = new ArrayList<>();

        pluginViewParam.getPluginViewFields().forEach(pluginViewField -> {
            if (StringUtils.equals(pluginViewField.getTypeField(), "xAxis")) {
                xAxis.add(pluginViewField);
            }
            if (StringUtils.equals(pluginViewField.getTypeField(), "yAxis")) {
                yAxis.add(pluginViewField);
            }
        });
        Map<String, Object> map = new HashMap<>();
        List<AxisChartDataAntVDTO> datas = new ArrayList<>();

        for (int i1 = 0; i1 < data.size(); i1++) {
            String[] row = data.get(i1);

            StringBuilder a = new StringBuilder();
            if (isDrill) {
                a.append(row[xAxis.size() - 1]);
            } else {
                for (int i = 0; i < xAxis.size(); i++) {
                    if (i == xAxis.size() - 1) {
                        a.append(row[i]);
                    } else {
                        a.append(row[i]).append("\n");
                    }
                }
            }

            if (CollectionUtils.isEmpty(yAxis)) {
                for (int i = 0; i < xAxis.size() + yAxis.size(); i++) {
                    SymbolMapResultDTO axisChartDataDTO = new SymbolMapResultDTO();
                    axisChartDataDTO.setField(a.toString());
                    axisChartDataDTO.setName(a.toString());

                    List<ChartDimensionDTO> dimensionList = new ArrayList<>();
                    List<ChartQuotaDTO> quotaList = new ArrayList<>();

                    for (int j = 0; j < xAxis.size(); j++) {
                        ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                        chartDimensionDTO.setId(xAxis.get(j).getId());
                        chartDimensionDTO.setValue(row[j]);
                        dimensionList.add(chartDimensionDTO);
                    }
                    axisChartDataDTO.setDimensionList(dimensionList);

                    int j = i - xAxis.size();
                    if (j > -1) {
                        ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                        chartQuotaDTO.setId(yAxis.get(j).getId());
                        quotaList.add(chartQuotaDTO);
                        axisChartDataDTO.setQuotaList(quotaList);
                        try {
                            axisChartDataDTO.setBusiValue(row[i]);
                            axisChartDataDTO.setValue(StringUtils.isEmpty(row[i]) ? null : new BigDecimal(row[i]));
                        } catch (Exception e) {
                            axisChartDataDTO.setValue(new BigDecimal(0));
                        }
                        axisChartDataDTO.setCategory(yAxis.get(j).getName());
                    }
                    axisChartDataDTO.setLongitude(dimensionList.get(0).getValue());
                    axisChartDataDTO.setLatitude(dimensionList.get(1).getValue());
                    datas.add(axisChartDataDTO);
                }
            } else {
                SymbolMapResultDTO axisChartDataDTO = new SymbolMapResultDTO();
                axisChartDataDTO.setField(a.toString());
                axisChartDataDTO.setName(a.toString());

                List<ChartDimensionDTO> dimensionList = new ArrayList<>();

                for (int j = 0; j < xAxis.size(); j++) {
                    ChartDimensionDTO chartDimensionDTO = new ChartDimensionDTO();
                    chartDimensionDTO.setId(xAxis.get(j).getId());
                    chartDimensionDTO.setValue(row[j]);
                    dimensionList.add(chartDimensionDTO);
                }
                axisChartDataDTO.setDimensionList(dimensionList);
                axisChartDataDTO.setQuotaList(new ArrayList<>());
                axisChartDataDTO.setProperties(new HashMap<>());
                int step = xAxis.size();
                for (int i = 0; i < yAxis.size(); i++) {
                    PluginViewField curY = yAxis.get(i);
                    ChartQuotaDTO chartQuotaDTO = new ChartQuotaDTO();
                    chartQuotaDTO.setId(curY.getId());
                    axisChartDataDTO.getQuotaList().add(chartQuotaDTO);
                    axisChartDataDTO.getProperties().put(curY.getName(), row[i + step]);
                    axisChartDataDTO.setLongitude(dimensionList.get(0).getValue());
                    axisChartDataDTO.setLatitude(dimensionList.get(1).getValue());
                    datas.add(axisChartDataDTO);
                }
            }
        }
        map.put("datas", datas);
        return map;
    }
}
