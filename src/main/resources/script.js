// Once the DOM has loaded we want to populated the table, we do that using AJAX.
window.onload = function (ev) {
    var updateLoop;

    function updateTable() {
        // find the table, and then count the rows in it, minus the header.
        // this is the number of pitstops
        var table = document.getElementById("table");
        var rowCount = table.rows.length - 1;

        // fire off an xhr request to the api endpoint, getting the list of pitstops back.
        var xhr = new XMLHttpRequest();

        xhr.open("GET", "/api");

        xhr.onreadystatechange = function (ev) {
            if (xhr.readyState === 4) {
                // if the request is successful (code 200)
                if (xhr.status === 200) {
                    // parse the JSON content of the response
                    var pitStops = JSON.parse(xhr.responseText);
                    // start n positions into the list

                    // as both the list and the table are sorted in the same way, this is the first pitstop not already in the table
                    for (var i = rowCount; i < pitStops.length; i++) {
                        // add a row, and write the json object to the row's dataset, so we can find it again.
                        var row = table.insertRow(i + 1);
                        row.dataset['json'] = JSON.stringify(pitStops[i]);

                        // add the information to the cell.
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
                // if it fails to get the api, stop updating as the server has probably gone down.
                else {
                    clearInterval(updateLoop);
                }
            }
        };
        xhr.send();
    }
    //repeat the ajax every second so that the contents of the table appears as if it's continuously updating.
    updateLoop = setInterval(updateTable, 1000);
    updateTable();
};

//Change the comment field from display to edit mode.
function createCommentBox(event) {
    //get the contents of the cell
    var tableCell = event.target.parentElement;
    var text = "";
    if (tableCell.children.length === 2) {
        //check for an existing comment.
        text = tableCell.children[0].innerText;
    }
    //create the edit form
    tableCell.innerHTML = document.getElementById("commentBox").innerHTML;
    //put the contents of the existing comment in the text area
    tableCell.getElementsByTagName("textarea")[0].value = text;
}

//add a comment
function addComment(event) {
    // get the table cell from the event
    var tableCell = event.target.parentElement;
    //get the content of the comment.
    var comment = tableCell.getElementsByTagName("textarea")[0].value;
    console.log(comment);
    //get the json object
    var pitStopObject = JSON.parse(tableCell.parentElement.dataset['json']);
    //update the comment
    pitStopObject.comment = comment;
    setComment(tableCell, comment);
    // write the new json to the row's dataset
    var pitStopObjectString = JSON.stringify(pitStopObject);
    tableCell.parentElement.dataset['json'] = pitStopObjectString;
    // send it to the server via xhr
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/add/comment");
    xhr.send(pitStopObjectString);


}
// cancel a comment edit
function cancelComment(event) {
    // get the old comment and then write it to the comment box
    var tableCell = event.target.parentElement;
    var pitStopObject = JSON.parse(tableCell.parentElement.dataset['json']);
    var comment = pitStopObject.comment;
    setComment(tableCell, comment);


}

function setComment(cell, comment) {
    // if there is no comment, we just need the add button.
    if (comment === "") {
        cell.innerHTML = "<button class=\"btn\" onclick='createCommentBox(event);'> Add </button>";
    }

    // if there is a comment, put it in as well as the edit button.
    else {
        cell.innerHTML = "<p>" + comment + "</p><button class=\"btn\" onclick='createCommentBox(event);'> Edit </button>";
    }

}


