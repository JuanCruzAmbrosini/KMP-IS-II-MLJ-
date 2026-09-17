/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 100.0, "KoPercent": 0.0};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [1.0, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "04_POST_Login (/users/login)-0"], "isController": false}, {"data": [1.0, 500, 1500, "02_GET_Login_Page (/login)"], "isController": false}, {"data": [1.0, 500, 1500, "04_POST_Login (/users/login)-1"], "isController": false}, {"data": [1.0, 500, 1500, "09_POST_Create_Pet (/pets/update)-1"], "isController": false}, {"data": [1.0, 500, 1500, "11_GET_Download_Votes_Report (/reports/votes)"], "isController": false}, {"data": [1.0, 500, 1500, "03_GET_Register_Page (/register)"], "isController": false}, {"data": [1.0, 500, 1500, "09_POST_Create_Pet (/pets/update)-0"], "isController": false}, {"data": [1.0, 500, 1500, "08_GET_Pet_Create_Form (/pets/edit)"], "isController": false}, {"data": [1.0, 500, 1500, "12_GET_Logout (/users/logout)"], "isController": false}, {"data": [1.0, 500, 1500, "12_GET_Logout (/users/logout)-1"], "isController": false}, {"data": [1.0, 500, 1500, "12_GET_Logout (/users/logout)-0"], "isController": false}, {"data": [1.0, 500, 1500, "01_GET_Index (/)"], "isController": false}, {"data": [1.0, 500, 1500, "04_POST_Login (/users/login)"], "isController": false}, {"data": [1.0, 500, 1500, "10_GET_Mascotas_De_Baja (/pets/baja)"], "isController": false}, {"data": [1.0, 500, 1500, "05_GET_Home (/home)"], "isController": false}, {"data": [1.0, 500, 1500, "07_GET_Pets_List (/pets/list)"], "isController": false}, {"data": [1.0, 500, 1500, "06_GET_Profile (/users/profile/edit)"], "isController": false}, {"data": [1.0, 500, 1500, "09_POST_Create_Pet (/pets/update)"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 900, 0, 0.0, 5.2388888888888845, 0, 34, 3.0, 14.0, 20.0, 25.0, 39.2704424469849, 584.322409309822, 23.797138166506677], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["04_POST_Login (/users/login)-0", 50, 0, 0.0, 8.2, 6, 24, 8.0, 9.0, 15.29999999999994, 24.0, 2.5724134382878012, 1.2510370041673098, 1.3037915766321964], "isController": false}, {"data": ["02_GET_Login_Page (/login)", 50, 0, 0.0, 1.3599999999999999, 1, 6, 1.0, 2.0, 2.0, 6.0, 2.574002574002574, 10.0697796010296, 0.9652509652509652], "isController": false}, {"data": ["04_POST_Login (/users/login)-1", 50, 0, 0.0, 0.9800000000000003, 0, 3, 1.0, 1.0, 1.4499999999999957, 3.0, 2.573737581716168, 7.97004089025583, 1.1109296983579555], "isController": false}, {"data": ["09_POST_Create_Pet (/pets/update)-1", 50, 0, 0.0, 7.06, 5, 14, 7.0, 8.899999999999999, 9.0, 14.0, 2.568845047266749, 192.68726077630498, 1.1213610704377313], "isController": false}, {"data": ["11_GET_Download_Votes_Report (/reports/votes)", 50, 0, 0.0, 2.36, 1, 4, 2.0, 3.0, 3.4499999999999957, 4.0, 2.567657782570739, 1.0932605402351976, 1.1308727147845736], "isController": false}, {"data": ["03_GET_Register_Page (/register)", 50, 0, 0.0, 2.5600000000000005, 2, 4, 3.0, 3.0, 3.4499999999999957, 4.0, 2.574797878366548, 12.93685066816005, 0.9730925575467325], "isController": false}, {"data": ["09_POST_Create_Pet (/pets/update)-0", 50, 0, 0.0, 15.300000000000002, 11, 20, 16.0, 19.0, 19.0, 20.0, 2.5675259320119133, 0.9001384859299579, 3.6161496578771697], "isController": false}, {"data": ["08_GET_Pet_Create_Form (/pets/edit)", 50, 0, 0.0, 1.62, 1, 2, 2.0, 2.0, 2.0, 2.0, 2.5709584533113943, 11.579355846359523, 1.1574334443130398], "isController": false}, {"data": ["12_GET_Logout (/users/logout)", 50, 0, 0.0, 3.0000000000000013, 2, 9, 3.0, 4.0, 4.449999999999996, 9.0, 2.5658131061733465, 11.345704828860267, 2.089734502488839], "isController": false}, {"data": ["12_GET_Logout (/users/logout)-1", 50, 0, 0.0, 0.78, 0, 2, 1.0, 1.0, 1.0, 2.0, 2.5660764690787783, 10.226716865537593, 0.962278675904542], "isController": false}, {"data": ["12_GET_Logout (/users/logout)-0", 50, 0, 0.0, 1.98, 1, 8, 2.0, 2.8999999999999986, 3.4499999999999957, 8.0, 2.565944780868316, 1.1200950361798214, 1.1276124525300215], "isController": false}, {"data": ["01_GET_Index (/)", 50, 0, 0.0, 2.4400000000000004, 1, 30, 2.0, 2.8999999999999986, 3.0, 30.0, 2.5716196060278764, 2.4510749369953193, 0.8186992105127809], "isController": false}, {"data": ["04_POST_Login (/users/login)", 50, 0, 0.0, 9.439999999999998, 7, 26, 9.0, 10.0, 17.39999999999995, 26.0, 2.572281098878485, 9.2165032732277, 2.4140255234592036], "isController": false}, {"data": ["10_GET_Mascotas_De_Baja (/pets/baja)", 50, 0, 0.0, 2.88, 2, 18, 3.0, 3.0, 4.0, 18.0, 2.568977033345322, 14.611056877151517, 1.121418685454452], "isController": false}, {"data": ["05_GET_Home (/home)", 50, 0, 0.0, 1.3000000000000005, 0, 2, 1.0, 2.0, 2.0, 2.0, 2.572810538231965, 7.9671701335288665, 1.1105295487290314], "isController": false}, {"data": ["07_GET_Pets_List (/pets/list)", 50, 0, 0.0, 7.660000000000002, 6, 15, 7.0, 10.0, 12.0, 15.0, 2.5691090329873596, 187.19095424737952, 1.1214763063919433], "isController": false}, {"data": ["06_GET_Profile (/users/profile/edit)", 50, 0, 0.0, 2.8599999999999994, 2, 6, 3.0, 4.0, 4.449999999999996, 6.0, 2.5716196060278764, 11.918854150594044, 1.1451743558092886], "isController": false}, {"data": ["09_POST_Create_Pet (/pets/update)", 50, 0, 0.0, 22.520000000000007, 18, 34, 22.5, 25.0, 27.0, 34.0, 2.566339886054509, 193.3990730701124, 4.734746718292871], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": []}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 900, 0, "", "", "", "", "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
