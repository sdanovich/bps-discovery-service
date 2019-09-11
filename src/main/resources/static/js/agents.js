axios.get("/bps/discovery/v0.1/discovery/agents")
    .then((results) => {
        var html = '<option disabled="disabled" selected="selected" >-- Select Agent --</option>';
        var len = results.data.length;
        for ( var i = 0; i < len; i++) {
            html += '<option value="' + results.data[i] + '">'
                + results.data[i] + '</option>';
        }
        html += '</option>';
        var agentName = document.getElementById('agentName');
        agentName.innerHTML = html;
    });