window.onload = function (ev) {
    function updateTable() {

        var table = document.getElementById("table");
        var rowCount = table.rows.length  - 1;
        console.log(rowCount);
        var xhr = new XMLHttpRequest();

        xhr.open("GET", "/api");

        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    var pitStops = JSON.parse(xhr.responseText);
                    for (var i = rowCount; i < pitStops.length; i++) {
                        var row = table.insertRow(i + 1);
                        var vehicleNumberCell = row.insertCell(0);
                        vehicleNumberCell.innerText = pitStops[i].vehicle_number;

                        var timeInCell = row.insertCell(1);
                        timeInCell.innerText = pitStops[i].time_in;

                        var timeOutCell = row.insertCell(2);
                        timeOutCell.innerText = pitStops[i].time_out;

                    }
                }
            }
        }
        xhr.send();
    }
    var updateLoop =  setInterval(updateTable, 1000);
    updateTable();
}


