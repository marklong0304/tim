import {ModalTemplate} from "../templates/modalTemplate.js";

export default {
    template: ModalTemplate,
    data() {
        return {
            modalInfo: null,
            initialized: false
        };
    },

    mounted() {
        this.$eventHub.$on('getModalInfo', (modalInfo) => {
            this.modalInfo = modalInfo;
            this.initialized = true;
        });
        $('#certificateDialogCompare').on('hidden.bs.modal', () => {
            this.initialized = false;
        });
    }
}