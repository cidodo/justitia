import React from 'react';
import { connect } from 'dva';
import {
  Icon,
  Button,
  Form,
  Upload,
  Select,
  Radio,
  Input,
} from 'antd';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

@connect(({ organizationManager }) => {
  return {
    organizationManager
  };
})
class SettingOrg extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tlsEnable: true,
      certFileList: [],
      keyFileList: [],
      clientFileList: [],
    };
  }

  componentDidMount() {
    const { updateList } = this.props.organizationManager;
    this.setState({
      tlsEnable: updateList.tlsEnable
    });
  }

  componentDidUpdate(prevProps, prevState) {
    const { form, organizationManager } = this.props;
    const { isFetching, updateList } = organizationManager;
    const oldFetching = prevProps.organizationManager.isFetching;
    const prevList = prevProps.organizationManager.updateList;

    if (oldFetching && !isFetching) {
      form.resetFields();
    }
    if (!prevList.id && updateList.id || prevList.id && !updateList.id) {
      this.setState({
        tlsEnable: updateList.tlsEnable
      });
    }
  }

  changeTlsEnable = (e) => {
    const value = e.target.value;
    this.setState({
      tlsEnable: value
    })
  }

  certChange = (info) => {
    let fileList = info.fileList;
    fileList = fileList.slice(-1);
    this.setState({
      certFileList: fileList
    })
  }

  keyChange = (info) => {
    let fileList = info.fileList;
    fileList = fileList.slice(-1);
    this.setState({
      keyFileList: fileList
    })
  }

  clientChange = (info) => {
    let fileList = info.fileList;
    fileList = fileList.slice(-1);
    this.setState({
      clientFileList: fileList
    })
  }

  beforeUpload = (file) => {
    return false;
  }
  // 提交表单
  handleSubmit = (e) => {
    e.preventDefault();
    const { form, dispatch, organizationManager } = this.props;

    form.validateFields((err, values) => {
      if (!err) {
        let formData = new FormData();
        Object.keys(values).forEach(key => {
          if (values[key] !== undefined) {
            formData.append(key, values[key] && values[key].file ? values[key].file : values[key])
          }
        });
        if (organizationManager.isUpdate) {
          dispatch({
            type: 'organizationManager/handleUpdateOrg',
            payload: {
              data: formData,
              orgId: values.name
            }
          });
        } else {
          dispatch({
            type: 'organizationManager/handleSetOrg',
            payload: formData
          });
        }
      }
    })
  }
  // 清空内容
  handleClean = () => {
    const { form, dispatch } = this.props;
    form.resetFields();
    dispatch({
      type: 'organizationManager/handlecleanOrgList'
    });
  }

  render() {
    const { form, organizationManager } = this.props;
    const { isFetching, isUpdate, updateList, orgList } = organizationManager;
    const { getFieldDecorator } = form;
    const { certFileList, keyFileList, clientFileList, tlsEnable } = this.state;
    const formItemLayout = {
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
    };

    return (
      <Form onSubmit={this.handleSubmit}>
        <FormItem {...formItemLayout} label="组织名称">
          {getFieldDecorator('name', {
            initialValue: updateList.id,
            rules: [{
              required: true,
              message: '请输入组织名称！'
            }, {
              pattern: /^.{1,64}$/g, message: '请输入数字字母下划线组成的不超过16位字符的名称！'
            }]
          })(<Input placeholder="请输入不超过64位字符的名称" />)}
        </FormItem>
        <FormItem {...formItemLayout} label="组织MSPID">
          {getFieldDecorator('mspId', {
            initialValue: updateList.mspId,
            rules: [{
              required: true,
              message: '请输入组织MSPID！'
            }, {
              pattern: /^.{1,64}$/g,
              message: '输入组织的MSPID！',
            }]
          })(<Input placeholder="请输入组织MSPID" />)}
        </FormItem>
        <FormItem {...formItemLayout} label="组织类型">
          {getFieldDecorator('type', {
            initialValue: updateList.type,
            rules: [{
              required: true,
              message: '请选择组织！'
            }]
          })(
            <Select disabled>
              <Option value="PEER_ORGANIZATION">peer组织</Option>
              <Option value="ORDERER_ORGANIZATION">orderer组织</Option>
            </Select>
          )}
        </FormItem>
        <FormItem {...formItemLayout} label="是否使用tls">
          {getFieldDecorator('tlsEnable', {
            initialValue: !updateList.tlsEnable ? false : true,
          })(<RadioGroup onChange={this.changeTlsEnable}>
            <Radio value={true}>是</Radio>
            <Radio value={false}>否</Radio>
          </RadioGroup>)}
        </FormItem>
        <div style={{ display: tlsEnable ? 'block' : 'none' }}>
          <FormItem {...formItemLayout} label="签名根证书">
            {getFieldDecorator('caCert', {
              rules: [{
                required: tlsEnable,
                message: '请上传签名根证书！'
              }]
            })(
              <Upload
                name="caCert"
                fileList={certFileList}
                onChange={this.certChange}
                beforeUpload={this.beforeUpload}
              >
                <Button>
                  <Icon type="upload" /> 上传签名证书
                </Button>
              </Upload>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="tls根证书">
            {getFieldDecorator('tlsCaCert', {
              rules: [{
                required: tlsEnable,
                message: '请上传tls根证书'
              }]
            })(
              <Upload
                name="tlsCaCert"
                fileList={clientFileList}
                onChange={this.clientChange}
                beforeUpload={this.beforeUpload}
              >
                <Button>
                  <Icon type="upload" /> 上传tls根证书
                </Button>
              </Upload>
            )}
          </FormItem>
        </div>

        <FormItem wrapperCol={{ span: 8, offset: 8 }}>
          <Button onClick={this.handleClean} style={{ marginRight: 10 }}>清空内容</Button>
          <Button type="primary" htmlType="submit" loading={isFetching} disabled={!isUpdate && orgList.length !== 0}>
            {isUpdate ? "更新组织" : "新增组织"}
          </Button>
        </FormItem>
      </Form>
    )
  }
}

const WrapSettingOrg = Form.create({})(SettingOrg);
export default WrapSettingOrg;
