window.onload = function (ev) {
    function updateTable() {

        var table = document.getElementById("table");
        var rowCount = table.rows.length  - 1;
        var xhr = new XMLHttpRequest();

        xhr.open("GET", "/api");

        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var pitStops = JSON.parse(xhr.responseText);
                    for (var i = rowCount; i < pitStops.length; i++) {
                        var row = table.insertRow(i + 1);
                        row.dataset['json'] = JSON.stringify(pitStops[i]);
                        var vehicleNumberCell = row.insertCell(0);
                        vehicleNumberCell.innerText = pitStops[i].vehicle_number;

                        var timeInCell = row.insertCell(1);
                        timeInCell.innerText = pitStops[i].time_in;

                        var timeOutCell = row.insertCell(2);
                        timeOutCell.innerText = pitStops[i].time_out;

                        var commentCell = row.insertCell(3);
                        setComment(commentCell, pitStops[i].comment);
                    }
                }
            }
        };
        xhr.send();
    }
    var updateLoop =  setInterval(updateTable, 1000);
    updateTable();
};

function createCommentBox(event){
    var tableCell = event.target.parentElement;
    var text = "";
    if (tableCell.children.length === 2){
        text = tableCell.children[0].innerText;
    }
    tableCell.innerHTML = document.getElementById("commentBox").innerHTML;
    tableCell.getElementsByTagName("textarea")[0].value = text;



}

function addComment(event) {
    var tableCell = event.target.parentElement;
    var comment = tableCell.getElementsByTagName("textarea")[0].value;
    console.log(comment);
    var pitStopObject = JSON.parse(tableCell.parentElement.dataset['json']);
    pitStopObject.comment = comment;
    setComment(tableCell, comment);
    var pitStopObjectString = JSON.stringify(pitStopObject);
    tableCell.parentElement.dataset['json'] = pitStopObjectString;
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/add/comment");
    xhr.send(pitStopObjectString);


}

function cancelComment(event){
    var tableCell = event.target.parentElement;
    var pitStopObject = JSON.parse(tableCell.parentElement.dataset['json']);
    var comment = pitStopObject.comment;
    setComment(tableCell, comment);


}

function setComment(cell, comment){
    if (comment === ""){
        cell.innerHTML = "<button class=\"btn\" onclick='createCommentBox(event);'> Add </button>";
    }
    else{
        cell.innerHTML = "<p>"+comment+"</p><button class=\"btn\" onclick='createCommentBox(event);'> Edit </button>";
    }

}


