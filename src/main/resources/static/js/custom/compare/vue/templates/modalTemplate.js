const ModalTemplate = `
<div class="modal noPadding" id="certificateDialogCompare" tabindex="-1" role="dialog" aria-labelledby="chooseCategoryDialogLabel">
        <div v-if="initialized" class="modal-dialog custom-dialog-category">
            <div class="container-fluid popup-nav">
                <div class="logo">
                    <a><img src="https://d3u70gwj4bg98w.cloudfront.net/images/newdesign/logo-gray.svg" alt="Logo Travel Insurance Master"/>travel insurance master</a>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">&times;</span></button>
                </div>
            </div>

            <div class="container-fluid popup-inner">
                <div class="img-box">
                    <img id="modalVendorCode" alt="Vendor" width="110px" height="" :src="modalInfo.vendorCode"/>
                </div>
                <p class="plan-name" id="modalPolicyMetaName">{{modalInfo.policyMetaName}}</p>
            </div>
            <div class="container-fluid popup-text">
                <h1 id="modalCategory" class="popup-head">{{modalInfo.category}}</h1>
                <p id="modalContent" class="popup-info"><span v-html="modalInfo.content"></span></p>
            </div>
        </div>
    </div>
`;

export {ModalTemplate}