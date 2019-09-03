Vue.component('modal1', {
    template: '#modal-template1',
    methods: {
        onFileChange(e) {
            processFileSubmission(e);
        }
    }
});

Vue.component('modal2', {
    template: '#modal-template2',
    methods: {
        onFileChange(e) {
            processFileSubmission(e);
        }
    }
});
Vue.component('modal3', {
    template: '#modal-template3',
    methods: {
        onFileChange(e) {
            processFileSubmission(e);
        }
    }
});
Vue.component('modal4', {
    template: '#modal-template4',
    methods: {
        onFileChange(e) {
            processFileSubmission(e);
        }
    }
});
// start app
new Vue({
        el: '#uploadBPADiscovery',
        data: {
            showModal: false
        }
    }
);
new Vue({
        el: '#uploadSPADiscovery',
        data: {
            showModal: false
        }
    }
);
new Vue({
        el: '#uploadBPARegister',
        data: {
            showModal: false
        }
    }
);
new Vue({
        el: '#uploadSPARegister',
        data: {
            showModal: false
        }
    }
);

function processFileSubmission(e) {
    const formData = new FormData();
    var action = e.target.outerHTML.substring(e.target.outerHTML.indexOf("action=\"") + 8, e.target.outerHTML.lastIndexOf("\""));
    var file = e.target.files[0];
    formData.append('file', file);
    axios.post(
        action,
        formData,
        {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        }
    ).then(function () {
        console.log('SUCCESS!!');
    }).catch(function () {
        console.log('FAILURE!!');
    });
}