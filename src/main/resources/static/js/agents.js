Vue.component('htmlmodal', {
	template: '#html-modal-template',
	data() {
       return {
         options: null,
         agent: null
       };
    },
    created() {
      this.getOptions();
    },
    methods: {
      getOptions() {
        axios.get('/bps/discovery/v0.1/discovery/agents')
            .then(response => {this.options = response.data}).catch(function(error) {
                console.log('an error occurred ' + error);
            });
      },
      onChange:function(event){

       console.log('Selected Agent '+event.target.options[event.target.options.selectedIndex].text);
       this.agent = event.target.options[event.target.options.selectedIndex].text;
        //Store in global var
       store.state.message = this.agent;
      }
    },

	});

	// START THE Buyer Registration
	new Vue({
		el:'#button-container1',
		data: {
			showModal: false
		},
        methods: {
            onLoad(e) {
            }
        }
	});

	// START THE Supplier Registration
	new Vue({
		el:'#button-container2',
		data: {
			showModal: false
		}
	});